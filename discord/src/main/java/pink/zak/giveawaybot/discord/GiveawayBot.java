package pink.zak.giveawaybot.discord;

import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import pink.zak.giveawaybot.discord.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.discord.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.discord.cache.ServerCache;
import pink.zak.giveawaybot.discord.commands.console.HeapDumpCommand;
import pink.zak.giveawaybot.discord.commands.console.ReloadCommand;
import pink.zak.giveawaybot.discord.commands.console.StopCommand;
import pink.zak.giveawaybot.discord.commands.console.stats.StatsCommand;
import pink.zak.giveawaybot.discord.commands.console.unload.UnloadCommand;
import pink.zak.giveawaybot.discord.commands.discord.about.BotAboutCommand;
import pink.zak.giveawaybot.discord.commands.discord.admin.AdminCommand;
import pink.zak.giveawaybot.discord.commands.discord.ban.BanCommand;
import pink.zak.giveawaybot.discord.commands.discord.ban.UnbanCommand;
import pink.zak.giveawaybot.discord.commands.discord.entries.EntriesCommand;
import pink.zak.giveawaybot.discord.commands.discord.giveaway.GiveawayCommand;
import pink.zak.giveawaybot.discord.commands.discord.help.HelpCommand;
import pink.zak.giveawaybot.discord.commands.discord.premium.PremiumCommand;
import pink.zak.giveawaybot.discord.commands.discord.preset.PresetCommand;
import pink.zak.giveawaybot.discord.controllers.GiveawayController;
import pink.zak.giveawaybot.discord.controllers.ScheduledGiveawayController;
import pink.zak.giveawaybot.discord.defaults.Defaults;
import pink.zak.giveawaybot.discord.lang.LanguageRegistry;
import pink.zak.giveawaybot.discord.listener.GiveawayDeletionListener;
import pink.zak.giveawaybot.discord.listener.GuildJoinListener;
import pink.zak.giveawaybot.discord.listener.ReactionAddListener;
import pink.zak.giveawaybot.discord.listener.message.MessageSendListener;
import pink.zak.giveawaybot.discord.metrics.MetricsLogger;
import pink.zak.giveawaybot.discord.metrics.helpers.LatencyMonitor;
import pink.zak.giveawaybot.discord.pipelines.entries.EntryPipeline;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.command.discord.command.Command;
import pink.zak.giveawaybot.discord.service.config.Config;
import pink.zak.giveawaybot.discord.service.config.Reloadable;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoConnectionFactory;
import pink.zak.giveawaybot.discord.shutdown.ShutdownHelper;
import pink.zak.giveawaybot.discord.shutdown.ShutdownHook;
import pink.zak.giveawaybot.discord.storage.finishedgiveaway.FullFinishedGiveawayStorage;
import pink.zak.giveawaybot.discord.storage.GiveawayStorage;
import pink.zak.giveawaybot.discord.storage.ScheduledGiveawayStorage;
import pink.zak.giveawaybot.discord.storage.ServerStorage;
import pink.zak.giveawaybot.discord.threads.ThreadFunction;
import pink.zak.giveawaybot.discord.threads.ThreadManager;
import pink.zak.metrics.Metrics;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.UnaryOperator;

public class GiveawayBot extends JdaBot {
    private Metrics metrics;
    private LatencyMonitor latencyMonitor;
    private ThreadManager threadManager;
    private MongoConnectionFactory mongoConnectionFactory;
    private ScheduledGiveawayStorage scheduledGiveawayStorage;
    private ScheduledGiveawayCache scheduledGiveawayCache;
    private FullFinishedGiveawayStorage fullFinishedGiveawayStorage;
    private FinishedGiveawayCache finishedGiveawayCache;
    private GiveawayStorage giveawayStorage;
    private GiveawayCache giveawayCache;
    private ServerStorage serverStorage;
    private ServerCache serverCache;
    private LanguageRegistry languageRegistry;
    private GiveawayController giveawayController;
    private ScheduledGiveawayController scheduledGiveawayController;
    private MetricsLogger metricsLogger;
    private Defaults defaults;
    private EntryPipeline entryPipeline;

    public static GiveawayBot apiInstance;
    private static boolean locked = false;

    public static boolean isLocked() { return locked; }

    public GiveawayBot(UnaryOperator<Path> path) {
        super(path);
        apiInstance = this;
    }

    public void load() {
        this.configRelations();
        this.threadManager = new ThreadManager();

        Config settings = this.getConfigStore().getConfig("settings");

        super.buildJdaEarly(settings.string("token"), this.getGatewayIntents(), shard -> shard
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .disableCache(CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY)
                .enableCache(CacheFlag.EMOTE, CacheFlag.MEMBER_OVERRIDES));

        if (settings.bool("enable-metrics")) {
            this.metrics = new Metrics(new Metrics.Config(settings.string("influx-url"),
                    settings.string("influx-token").toCharArray(),
                    settings.string("influx-org"),
                    settings.string("influx-bucket"), 10));
        }
        this.setupStorage();
        this.fullFinishedGiveawayStorage = new FullFinishedGiveawayStorage(this);
        this.scheduledGiveawayStorage = new ScheduledGiveawayStorage(this);
        this.giveawayStorage = new GiveawayStorage(this);
        this.serverStorage = new ServerStorage(this);

        this.defaults = new Defaults(this);
        this.languageRegistry = new LanguageRegistry();
        this.languageRegistry.startLang(this);

        this.finishedGiveawayCache = new FinishedGiveawayCache(this);
        this.scheduledGiveawayCache = new ScheduledGiveawayCache(this);
        this.giveawayCache = new GiveawayCache(this);
        this.serverCache = new ServerCache(this);

        this.metricsLogger = new MetricsLogger(this);
        this.entryPipeline = new EntryPipeline(this);

        super.buildVariables(this, settings.string("prefix"));

        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
    }

    @Override
    public void onConnect() {
        this.latencyMonitor = new LatencyMonitor(this);
        this.closeIfPingUnusable();

        this.getShardManager().setPresence(OnlineStatus.IDLE, Activity.playing("Loading...."));
        this.giveawayController = new GiveawayController(this); // Makes use of JDA, retrieving messages
        this.scheduledGiveawayController = new ScheduledGiveawayController(this); // Makes use of JDA, retrieving messages
        this.metricsLogger.checkAndStart(this);

        this.registerCommands(
                new BotAboutCommand(this),
                new AdminCommand(this),
                new BanCommand(this),
                new UnbanCommand(this),
                new EntriesCommand(this),
                new GiveawayCommand(this),
                new HelpCommand(this),
                new PremiumCommand(this),
                new PresetCommand(this)
        );
        this.registerConsoleCommands(
                new pink.zak.giveawaybot.discord.commands.console.premium.PremiumCommand(this),
                new StatsCommand(this),
                new UnloadCommand(this),
                new HeapDumpCommand(this),
                new pink.zak.giveawaybot.discord.commands.console.HelpCommand(this),
                new ReloadCommand(this),
                new StopCommand(this)
        );

        this.messageEventRegistry.setServerCache(this.serverCache);
        this.registerListeners(
                this.latencyMonitor,
                new MessageSendListener(this),
                new GiveawayDeletionListener(this),
                new GuildJoinListener(this),
                new ReactionAddListener(this)
        );
        this.getShardManager().setPresence(OnlineStatus.ONLINE, Activity.playing("smartgiveaways.xyz"));
        logger.info("Finished startup. The bot is now fully registered.");
    }

    public void reload() {
        this.languageRegistry.reloadLanguages(this);
        for (Command command : this.getDiscordCommandBase().getCommands()) {
            if (command instanceof Reloadable reloadableCommand) {
                reloadableCommand.reload(this);
            }
        }
    }

    @Override
    public void unload() {
        locked = true;
        logger.info("Shutting down....");
        new ShutdownHelper(this).shutdown();
        this.mongoConnectionFactory.close();
        this.threadManager.shutdownPools();
    }

    @SneakyThrows
    private void closeIfPingUnusable() {
        while (this.latencyMonitor.getLastTiming() == Long.MAX_VALUE) {
            Thread.sleep(10);
        }
        for (int i = 1; i <= 10; i++) {
            if (this.latencyMonitor.isLatencyUsable()) {
                logger.info("Successfully tested latency on attempt no. {}", i);
                return;
            }
            logger.error("Failed testing latency on attempt no. {}", i);
            Thread.sleep(1000);
            if (i == 5) {
                System.exit(4);
            }
        }
    }

    private void configRelations() {
        this.getConfigStore().config("settings", Path::resolve, true);
    }

    private void setupStorage() {
        Config settings = this.getConfigStore().getConfig("settings");
        this.storageSettings.setAddress(settings.string("mongo-ip") + ":" + settings.string("mongo-port"));
        this.storageSettings.setDatabase(settings.string("mongo-storage-database"));
        this.storageSettings.setAuthDatabase(settings.string("mongo-auth-database"));
        this.storageSettings.setUsername(settings.string("mongo-username"));
        this.storageSettings.setPassword(settings.string("mongo-password"));
        this.mongoConnectionFactory = new MongoConnectionFactory(this.getStorageSettings());
    }

    public Defaults getDefaults() {
        return this.defaults;
    }

    public Metrics getMetrics() {
        return this.metrics;
    }

    public LatencyMonitor getLatencyMonitor() {
        return this.latencyMonitor;
    }

    public ExecutorService getAsyncExecutor(ThreadFunction function) {
        return this.threadManager.getAsyncExecutor(function);
    }

    public ThreadManager getThreadManager() {
        return this.threadManager;
    }

    public MongoConnectionFactory getMongoConnectionFactory() {
        return this.mongoConnectionFactory;
    }

    public FullFinishedGiveawayStorage getFinishedGiveawayStorage() {
        return this.fullFinishedGiveawayStorage;
    }

    public FinishedGiveawayCache getFinishedGiveawayCache() {
        return this.finishedGiveawayCache;
    }

    public ScheduledGiveawayStorage getScheduledGiveawayStorage() {
        return this.scheduledGiveawayStorage;
    }

    public ScheduledGiveawayCache getScheduledGiveawayCache() {
        return this.scheduledGiveawayCache;
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

    public ScheduledGiveawayController getScheduledGiveawayController() {
        return this.scheduledGiveawayController;
    }

    public MetricsLogger getMetricsLogger() {
        return this.metricsLogger;
    }

    public EntryPipeline getEntryPipeline() {
        return this.entryPipeline;
    }

    private Set<GatewayIntent> getGatewayIntents() {
        return Sets.newHashSet(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MESSAGE_REACTIONS);
    }
}
