package pink.zak.giveawaybot;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import pink.zak.giveawaybot.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.commands.ban.BanCommand;
import pink.zak.giveawaybot.commands.ban.ShadowBanCommand;
import pink.zak.giveawaybot.commands.ban.UnbanCommand;
import pink.zak.giveawaybot.commands.entries.EntriesCommand;
import pink.zak.giveawaybot.commands.giveaway.GiveawayCommand;
import pink.zak.giveawaybot.commands.help.HelpCommand;
import pink.zak.giveawaybot.commands.preset.PresetCommand;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.defaults.Defaults;
import pink.zak.giveawaybot.entries.pipeline.EntryPipeline;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.listener.GiveawayDeletionListener;
import pink.zak.giveawaybot.listener.MessageSendListener;
import pink.zak.giveawaybot.listener.ReactionAddListener;
import pink.zak.giveawaybot.metrics.MetricsStarter;
import pink.zak.giveawaybot.metrics.helpers.LatencyMonitor;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.config.Config;
import pink.zak.giveawaybot.storage.FinishedGiveawayStorage;
import pink.zak.giveawaybot.storage.GiveawayStorage;
import pink.zak.giveawaybot.storage.ServerStorage;
import pink.zak.giveawaybot.threads.ThreadFunction;
import pink.zak.giveawaybot.threads.ThreadManager;
import pink.zak.metrics.Metrics;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GiveawayBot extends JdaBot {
    private Metrics metricsLogger;
    private LatencyMonitor latencyMonitor;
    private Consumer<Throwable> deleteFailureThrowable;
    private ThreadManager threadManager;
    private FinishedGiveawayStorage finishedGiveawayStorage;
    private FinishedGiveawayCache finishedGiveawayCache;
    private GiveawayStorage giveawayStorage;
    private GiveawayCache giveawayCache;
    private ServerStorage serverStorage;
    private ServerCache serverCache;
    private LanguageRegistry languageRegistry;
    private GiveawayController giveawayController;
    private Defaults defaults;
    private EntryPipeline entryPipeline;

    public GiveawayBot() {
        super(basePath -> basePath);
    }

    public static Logger getLogger() {
        return logger;
    }

    public void load() {
        this.configRelations();
        this.threadManager = new ThreadManager();

        Config settings = this.getConfigStore().getConfig("settings");
        this.metricsLogger = new Metrics(new Metrics.Config(settings.string("influx-url"),
                settings.string("influx-token").toCharArray(),
                settings.string("influx-org"),
                settings.string("influx-bucket"), 5));

        this.buildJdaEarly(settings.string("token"), this.getGatewayIntents(), shard -> shard
                .disableCache(CacheFlag.VOICE_STATE));
        Awaitility.await().atMost(30, TimeUnit.SECONDS).until(this::isConnected);
        this.latencyMonitor = new LatencyMonitor(this);
        this.closeIfPingUnusable();

        this.setupThrowable();
        this.setupStorage();

        this.finishedGiveawayStorage = new FinishedGiveawayStorage(this);
        this.finishedGiveawayCache = new FinishedGiveawayCache(this);
        this.giveawayStorage = new GiveawayStorage(this);
        this.giveawayCache = new GiveawayCache(this);
        this.serverStorage = new ServerStorage(this);
        this.serverCache = new ServerCache(this);
        this.languageRegistry = new LanguageRegistry();

        this.languageRegistry.loadLanguages(this);

        this.defaults = new Defaults(this);
        this.entryPipeline = new EntryPipeline(this);

        this.initialize(this, this.getConfigStore().getConfig("settings").string("token"), ">", this.getGatewayIntents(), shard -> shard
                .disableCache(CacheFlag.VOICE_STATE));

        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
    }

    @Override
    public void onConnect() {
        this.getJda().getPresence().setPresence(OnlineStatus.IDLE, Activity.playing("Loading...."));
        this.giveawayController = new GiveawayController(this); // Makes use of JDA, retrieving messages
        this.registerCommands(
                new BanCommand(this),
                new ShadowBanCommand(this),
                new UnbanCommand(this),
                new EntriesCommand(this),
                new GiveawayCommand(this),
                new HelpCommand(this),
                new PresetCommand(this)
        );

        this.registerListeners(
                new ReactionAddListener(this),
                new MessageSendListener(this),
                new GiveawayDeletionListener(this)
        );
        new MetricsStarter().checkAndStart(this);
        this.getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("smartgiveaways.xyz"));
        logger.info("Finished startup. The bot is now fully registered.");
    }

    @Override
    public void unload() {
        logger.info("Shutting down....");
        List<Long> timings = new ArrayList<>();
        timings.add(System.currentTimeMillis());
        this.giveawayCache.shutdown();
        timings.add(System.currentTimeMillis());
        this.finishedGiveawayCache.shutdown();
        timings.add(System.currentTimeMillis());
        this.serverCache.shutdown();
        timings.add(System.currentTimeMillis());
        this.giveawayStorage.closeBack();
        this.finishedGiveawayStorage.closeBack();
        this.serverStorage.closeBack();
        this.threadManager.shutdownPools();
        timings.add(System.currentTimeMillis());
        logger.info("Completing shut down sequence.");
        logger.info("Timings:");
        for (int i = 1; i < timings.size(); i++) {
            logger.info(i + ": " + (timings.get(i) - timings.get(i - 1)));
        }
    }

    private void closeIfPingUnusable() {
        for (int i = 1; i <= 5; i++) {
            if (this.latencyMonitor.isLatencyUsable()) {
                logger.info("Successfully tested latency on attempt no. ".concat(String.valueOf(i)));
                return;
            }
            logger.error("Failed testing latency on attempt no. ".concat(String.valueOf(i)));
        }
    }

    private void configRelations() {
        this.getConfigStore().config("settings", Path::resolve, true);
    }

    private void setupStorage() {
        Config settings = this.getConfigStore().getConfig("settings");
        this.storageSettings.setAddress(settings.string("mongo-ip") + ":" + settings.string("mongo-port"));
        this.storageSettings.setDatabase(settings.string("mongo-storage-database"));
        if (settings.has("mongo-username")) {
            this.storageSettings.setAuthDatabase(settings.string("mongo-auth-database"));
            this.storageSettings.setUsername(settings.string("mongo-username"));
            this.storageSettings.setPassword(settings.string("mongo-password"));
        }
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

    public Metrics getMetrics() {
        return this.metricsLogger;
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

    public FinishedGiveawayStorage getFinishedGiveawayStorage() {
        return this.finishedGiveawayStorage;
    }

    public FinishedGiveawayCache getFinishedGiveawayCache() {
        return this.finishedGiveawayCache;
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

    public LanguageRegistry getLanguageRegistry() {
        return this.languageRegistry;
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
