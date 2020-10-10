package pink.zak.giveawaybot;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.commands.giveaway.GiveawayCommand;
import pink.zak.giveawaybot.commands.preset.PresetCommand;
import pink.zak.giveawaybot.controller.GiveawayController;
import pink.zak.giveawaybot.controller.UserController;
import pink.zak.giveawaybot.defaults.Defaults;
import pink.zak.giveawaybot.entries.pipeline.EntryPipeline;
import pink.zak.giveawaybot.listener.ReactionAddListener;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.storage.GiveawayStorage;
import pink.zak.giveawaybot.storage.ServerStorage;
import pink.zak.giveawaybot.storage.redis.RedisManager;
import pink.zak.giveawaybot.threads.ThreadFunction;
import pink.zak.giveawaybot.threads.ThreadManager;
import redis.clients.jedis.Jedis;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GiveawayBot extends JdaBot {
    private Consumer<Throwable> deleteFailureThrowable;
    private final Defaults defaults = new Defaults();
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
        this.configRelations();
        this.setupThrowable();
        this.setupStorage();
        this.initialize(this, this.getConfigStore().commons().get("token"), ">", this.getGatewayIntents());
        this.threadManager = new ThreadManager();

        this.redisManager = new RedisManager(this);
        this.giveawayStorage = new GiveawayStorage(this);
        this.giveawayCache = new GiveawayCache(this);
        this.serverStorage = new ServerStorage(this);
        this.serverCache = new ServerCache(this);
        this.userController = new UserController(this);
        this.giveawayController = new GiveawayController(this);
        this.entryPipeline = new EntryPipeline(this);

        this.giveawayController.loadAllGiveaways();

        this.registerCommands(
                new GiveawayCommand(this),
                new PresetCommand(this)
        );

        this.registerListeners(
                new ReactionAddListener(this)
        );
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
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
                ex.printStackTrace();
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
        return Sets.newHashSet(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS);
    }
}
