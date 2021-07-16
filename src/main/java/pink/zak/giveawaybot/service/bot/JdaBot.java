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
import pink.zak.giveawaybot.listener.reaction.pageable.PageableReactionEventRegistry;
import pink.zak.giveawaybot.listener.reaction.pageable.PageableReactionListener;
import pink.zak.giveawaybot.listener.slash.SlashCommandEventRegistry;
import pink.zak.giveawaybot.listener.slash.SlashCommandListener;
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
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

public abstract class JdaBot implements SimpleBot {
    public static final Logger LOGGER = JDALogger.getLog(GiveawayBot.class);
    protected final MessageEventRegistry messageEventRegistry = new MessageEventRegistry();
    protected final PageableReactionEventRegistry pageableReactionEventRegistry = new PageableReactionEventRegistry();
    protected final SlashCommandEventRegistry slashCommandEventRegistry = new SlashCommandEventRegistry();
    protected final StorageSettings storageSettings;
    private final BackendFactory backendFactory;
    private final ConfigStore configStore;
    private final Path basePath;
    private boolean buildEarlyUsed;
    private boolean connected;
    private boolean initialized;
    private DiscordCommandBase discordCommandBase;
    private ConsoleCommandBase consoleCommandBase;
    private ShardManager shardManager;

    private ReadyListener readyListener;

    @SneakyThrows
    protected JdaBot(UnaryOperator<Path> subBasePath) {
        this.basePath = subBasePath.apply(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().toPath().toAbsolutePath());
        this.storageSettings = new StorageSettings();
        this.backendFactory = new BackendFactory(this);
        this.configStore = new ConfigStore(this.basePath);
        LOGGER.info("Base path set to: {}", this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
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
    public void initialize(GiveawayBot bot, String token, Set<GatewayIntent> intents, UnaryOperator<DefaultShardManagerBuilder> jdaOperator) {
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
                LOGGER.error("Unable to log into Discord, the following error occurred:", e);
            }
        }
        this.buildVariables(bot);
    }

    public void buildVariables(GiveawayBot bot) {
        this.consoleCommandBase = new ConsoleCommandBase(bot);
        this.discordCommandBase = new DiscordCommandBase(bot);
        this.shardManager.addEventListener(
                this.messageEventRegistry,
                this.pageableReactionEventRegistry,
                this.slashCommandEventRegistry
        );
        this.registerListeners(
                this.discordCommandBase
        );
        this.startConsoleThread();

        this.initialized = true;
        this.readyListener.readyIfReady();
    }

    @Override
    public void initialize(GiveawayBot bot, String token, Set<GatewayIntent> intents) {
        this.initialize(bot, token, intents, jdaBuilder -> jdaBuilder);
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
            } else if (listener instanceof PageableReactionListener reactionListener) {
                this.pageableReactionEventRegistry.addListener(reactionListener);
            } else if (listener instanceof SlashCommandListener slashCommandListener) {
                this.slashCommandEventRegistry.addListener(slashCommandListener);
            } else {
                this.shardManager.addEventListener(listener);
            }
        }
    }

    @Override
    public void deregisterListeners(@NotNull Object... listeners) {
        for (Object listener : listeners) {
            if (listener instanceof GiveawayMessageListener messageListener) {
                this.messageEventRegistry.removeListener(messageListener);
            } else if (listener instanceof PageableReactionListener reactionListener) {
                this.pageableReactionEventRegistry.removeListener(reactionListener);
            } else if (listener instanceof SlashCommandListener slashCommandListener) {
                this.slashCommandEventRegistry.removeListener(slashCommandListener);
            } else {
                this.shardManager.removeEventListener(listener);
            }
        }
    }

    private void startConsoleThread() {
        Thread thread = new Thread(new ConsoleListener(this));
        thread.setUncaughtExceptionHandler(this.getExceptionHandler());
        thread.start();
    }

    public UncaughtExceptionHandler getExceptionHandler() {
        return (thread, ex) -> {
            JdaBot.LOGGER.error("Console Exception:", ex);
            this.startConsoleThread();
        };
    }

    @Override
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
