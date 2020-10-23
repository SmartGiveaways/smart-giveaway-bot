package pink.zak.giveawaybot;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.commands.ban.BanCommand;
import pink.zak.giveawaybot.commands.ban.ShadowBanCommand;
import pink.zak.giveawaybot.commands.ban.UnbanCommand;
import pink.zak.giveawaybot.commands.entries.EntriesCommand;
import pink.zak.giveawaybot.commands.giveaway.GiveawayCommand;
import pink.zak.giveawaybot.commands.preset.PresetCommand;
import pink.zak.giveawaybot.controller.GiveawayController;
import pink.zak.giveawaybot.controller.UserController;
import pink.zak.giveawaybot.defaults.Defaults;
import pink.zak.giveawaybot.entries.pipeline.EntryPipeline;
import pink.zak.giveawaybot.listener.MessageSendListener;
import pink.zak.giveawaybot.listener.ReactionAddListener;
import pink.zak.giveawaybot.metrics.GiveawayQuery;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.config.Config;
import pink.zak.giveawaybot.storage.GiveawayStorage;
import pink.zak.giveawaybot.storage.ServerStorage;
import pink.zak.giveawaybot.storage.redis.RedisManager;
import pink.zak.giveawaybot.threads.ThreadFunction;
import pink.zak.giveawaybot.threads.ThreadManager;
import pink.zak.metrics.Metrics;
import pink.zak.metrics.queries.stock.SystemQuery;
import pink.zak.metrics.queries.stock.backends.ProcessStats;
import redis.clients.jedis.Jedis;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GiveawayBot extends JdaBot {
    private Defaults defaults;
    private Metrics metricsLogger;
    private Consumer<Throwable> deleteFailureThrowable;
    private ThreadManager threadManager;
    private RedisManager redisManager;
    private GiveawayStorage giveawayStorage;
    private GiveawayCache giveawayCache;
    private ServerStorage serverStorage;
    private ServerCache serverCache;
    private UserController userController;
    private GiveawayController giveawayController;
    private EntryPipeline entryPipeline;

    public GiveawayBot() {
        super(basePath -> basePath.resolve("data"));
    }

    public static Logger getLogger() {
        return logger;
    }

    public void load() {
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);
        this.configRelations();
        this.setupThrowable();
        this.setupStorage();
        Config settings = this.getConfigStore().getConfig("settings");

        this.metricsLogger = new Metrics(new Metrics.Config(settings.string("influx-url"),
                settings.string("influx-token").toCharArray(),
                settings.string("influx-org"),
                settings.string("influx-bucket"), 5));

        this.threadManager = new ThreadManager();
        this.redisManager = new RedisManager(this);
        this.giveawayStorage = new GiveawayStorage(this);
        this.giveawayCache = new GiveawayCache(this);
        this.serverStorage = new ServerStorage(this);
        this.serverCache = new ServerCache(this);
        this.userController = new UserController(this);

        this.initialize(this, this.getConfigStore().commons().get("token"), ">", this.getGatewayIntents(), shard -> shard
                .disableCache(CacheFlag.VOICE_STATE)); // This should basically be called as late as physically possible

        this.defaults = new Defaults(this);
        this.entryPipeline = new EntryPipeline(this);

        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
    }

    @Override
    public void onConnect() {
        this.giveawayController = new GiveawayController(this); // Makes use of JDA, retrieving messages
        this.registerCommands(
                new BanCommand(this),
                new ShadowBanCommand(this),
                new UnbanCommand(this),
                new EntriesCommand(this),
                new GiveawayCommand(this),
                new PresetCommand(this)
        );

        this.registerListeners(
                new ReactionAddListener(this),
                new MessageSendListener(this)
        );

        this.threadManager.getUpdaterExecutor().scheduleAtFixedRate(() -> {
            this.metricsLogger.<ProcessStats>log(query -> query
                    .primary(new ProcessStats())
                    .push(SystemQuery.ALL));
            this.metricsLogger.<GiveawayCache>log(query -> query
                    .primary(this.giveawayCache)
                    .push(GiveawayQuery.ALL)
            );
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void unload() {
        logger.info("Shutting down....");
        this.giveawayCache.shutdown();
        this.serverCache.shutdown();
        this.giveawayStorage.closeBack();
        this.serverStorage.closeBack();
        this.redisManager.shutdown();
        this.threadManager.shutdownPools();
        logger.info("Completing shut down sequence.");
    }

    public void configRelations() {
        this.getConfigStore().config("settings", Path::resolve, true);

        this.getConfigStore().common("token", "settings", config -> config.string("token"));
    }

    private void setupStorage() {
        this.storageSettings.setAddress("127.0.0.1:27017");
        this.storageSettings.setDatabase("giveaway-bot");
    }

    private void setupThrowable() {
        this.deleteFailureThrowable = ex -> {
            if (!(ex instanceof ErrorResponseException)) {
                GiveawayBot.getLogger().error("", ex);
            }
        };
    }

    public Defaults getDefaults() {
        return this.defaults;
    }

    public Consumer<Throwable> getDeleteFailureThrowable() {
        return this.deleteFailureThrowable;
    }

    public void runOnMainThread(Runnable runnable) {
        this.threadManager.runOnMainThread(runnable);
    }

    public CompletableFuture<?> runAsync(ThreadFunction function, Supplier<?> supplier) {
        return CompletableFuture.supplyAsync(supplier, this.threadManager.getAsyncExecutor(function));
    }

    public Future<?> runAsync(ThreadFunction function, Runnable runnable) {
        return this.threadManager.runAsync(function, runnable);
    }

    public ExecutorService getAsyncExecutor(ThreadFunction function) {
        return this.threadManager.getAsyncExecutor(function);
    }

    public ThreadManager getThreadManager() {
        return this.threadManager;
    }

    public RedisManager getRedisManager() {
        return this.redisManager;
    }

    public Jedis getJedis() {
        return this.redisManager.getConnection();
    }

    public GiveawayStorage getGiveawayStorage() {
        return this.giveawayStorage;
    }

    public GiveawayCache getGiveawayCache() {
        return this.giveawayCache;
    }

    public ServerStorage getServerStorage() {
        return this.serverStorage;
    }

    public ServerCache getServerCache() {
        return this.serverCache;
    }

    public UserController getUserController() {
        return this.userController;
    }

    public GiveawayController getGiveawayController() {
        return this.giveawayController;
    }

    public EntryPipeline getEntryPipeline() {
        return this.entryPipeline;
    }

    private Set<GatewayIntent> getGatewayIntents() {
        return Sets.newHashSet(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MESSAGE_REACTIONS);
    }
}
