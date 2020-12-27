package pink.zak.giveawaybot.service.bot;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.listener.message.GiveawayMessageListener;
import pink.zak.giveawaybot.listener.message.MessageEventRegistry;
import pink.zak.giveawaybot.service.command.console.ConsoleCommandBase;
import pink.zak.giveawaybot.service.command.console.command.ConsoleBaseCommand;
import pink.zak.giveawaybot.service.command.discord.DiscordCommandBase;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.service.config.Config;
import pink.zak.giveawaybot.service.config.ConfigStore;
import pink.zak.giveawaybot.service.listener.ConsoleListener;
import pink.zak.giveawaybot.service.listener.ReadyListener;
import pink.zak.giveawaybot.service.registry.Registry;
import pink.zak.giveawaybot.service.storage.BackendFactory;
import pink.zak.giveawaybot.service.storage.settings.StorageSettings;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

public abstract class JdaBot implements SimpleBot {
    protected static final Logger logger = JDALogger.getLog(GiveawayBot.class);
    private boolean buildEarlyUsed;
    protected final MessageEventRegistry messageEventRegistry = new MessageEventRegistry();
    protected final StorageSettings storageSettings;
    private final BackendFactory backendFactory;
    private final ConfigStore configStore;
    private final Path basePath;
    private boolean connected;
    private boolean initialized;
    private DiscordCommandBase discordCommandBase;
    private ConsoleCommandBase consoleCommandBase;
    private String prefix;
    private ShardManager shardManager;

    private ReadyListener readyListener;

    @SneakyThrows
    public JdaBot(UnaryOperator<Path> subBasePath) {
        this.storageSettings = new StorageSettings();
        this.backendFactory = new BackendFactory(this);
        this.configStore = new ConfigStore(this);
        logger.info("Base path set to: {}", this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        this.basePath = subBasePath.apply(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().toPath().toAbsolutePath());
    }

    @SneakyThrows
    public void buildJdaEarly(String token, Set<GatewayIntent> intents, UnaryOperator<DefaultShardManagerBuilder> jdaOperator) {
        this.buildEarlyUsed = true;
        this.readyListener = new ReadyListener(this);
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token)
                .setEnabledIntents(intents)
                .addEventListeners(this.readyListener);
        jdaOperator.apply(builder);
        this.shardManager = builder.build();
        this.readyListener.setRequiredShards(this.shardManager.getShardsTotal());
    }

    @SneakyThrows
    @Override
    public void initialize(GiveawayBot bot, String token, String prefix, Set<GatewayIntent> intents, UnaryOperator<DefaultShardManagerBuilder> jdaOperator) {
        if (this.shardManager == null) {
            try {
                DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token)
                        .setEnabledIntents(intents);
                if (!this.buildEarlyUsed) {
                    builder.addEventListeners(new ReadyListener(this));
                }
                jdaOperator.apply(builder);
                this.shardManager = builder.build();
            } catch (LoginException e) {
                logger.error("Unable to log into Discord, the following error occurred:", e);
            }
        }
        this.buildVariables(bot, prefix);
    }

    public void buildVariables(GiveawayBot bot, String prefix) {
        this.prefix = prefix;
        this.consoleCommandBase = new ConsoleCommandBase(bot);
        this.discordCommandBase = new DiscordCommandBase(bot);
        this.shardManager.addEventListener(this.messageEventRegistry);
        this.messageEventRegistry.addListener(this.discordCommandBase);
        new Thread(new ConsoleListener(bot)).start();
        this.initialized = true;
        this.readyListener.readyIfReady();
    }

    @Override
    public void initialize(GiveawayBot bot, String token, String prefix, Set<GatewayIntent> intents) {
        this.initialize(bot, token, prefix, intents, jdaBuilder -> jdaBuilder);
    }

    @Override
    public void registerRegistries(Registry... registries) {
        for (Registry registry : registries) {
            registry.register();
        }
    }

    @Override
    public void registerCommands(SimpleCommand... commands) {
        for (SimpleCommand command : commands) {
            this.discordCommandBase.registerCommand(command);
        }
    }

    @Override
    public void registerConsoleCommands(@NotNull ConsoleBaseCommand... commands) {
        this.consoleCommandBase.registerCommands(commands);
    }

    @Override
    public void registerListeners(Object... listeners) {
        for (Object listener : listeners) {
            if (listener instanceof GiveawayMessageListener messageListener) {
                this.messageEventRegistry.addListener(messageListener);
            } else {
                this.shardManager.addEventListener(listener);
            }
        }
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public StorageSettings getStorageSettings() {
        return this.storageSettings;
    }

    @Override
    public BackendFactory getBackendFactory() {
        return this.backendFactory;
    }

    @Override
    public Path getBasePath() {
        return this.basePath;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public DiscordCommandBase getDiscordCommandBase() {
        return this.discordCommandBase;
    }

    @Override
    public ConsoleCommandBase getConsoleCommandBase() {
        return this.consoleCommandBase;
    }

    @Override
    public ConfigStore getConfigStore() {
        return this.configStore;
    }

    @Override
    public Config getConfig(String name) {
        return this.configStore.getConfig(name);
    }

    @Override
    public ShardManager getShardManager() {
        return this.shardManager;
    }

    @Override
    public JDA getJda() {
        Optional<JDA> optionalJDA = this.shardManager.getShards().stream().findAny();
        return optionalJDA.orElse(null);
    }
}
