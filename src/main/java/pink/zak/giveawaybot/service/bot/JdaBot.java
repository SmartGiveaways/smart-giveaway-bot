package pink.zak.giveawaybot.service.bot;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.command.CommandBase;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;
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
    public static final Logger logger = JDALogger.getLog(GiveawayBot.class);
    protected final StorageSettings storageSettings;
    private final BackendFactory backendFactory;
    private final ConfigStore configStore;
    private final Path basePath;
    private CommandBase commandBase;
    private String prefix;
    private ShardManager shardManager;

    @SneakyThrows
    public JdaBot(UnaryOperator<Path> subBasePath) {
        this.storageSettings = new StorageSettings();
        this.backendFactory = new BackendFactory(this);
        this.configStore = new ConfigStore(this);
        logger.info("Base path set to: {}", this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        this.basePath = subBasePath.apply(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().toPath().toAbsolutePath());
    }

    @SneakyThrows
    @Override
    public void initialize(GiveawayBot bot, String token, String prefix, Set<GatewayIntent> intents, UnaryOperator<DefaultShardManagerBuilder> jdaOperator) {
        this.commandBase = new CommandBase(bot);
        this.prefix = prefix;
        try {
            DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token)
                    .setEnabledIntents(intents)
                    .addEventListeners(this.commandBase, new ReadyListener(this));
            jdaOperator.apply(builder);
            this.shardManager = builder.build();
        } catch (LoginException e) {
            logger.error("Unable to log into Discord, the following error occurred:", e);
        }
        new Thread(new ConsoleListener(this)).start();
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
            this.commandBase.registerCommand(command);
        }
    }

    @Override
    public void registerListeners(Object... listeners) {
        this.shardManager.addEventListener(listeners);
    }


    @Override
    public boolean isInitialized() {
        return this.shardManager != null;
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
    public CommandBase getCommandBase() {
        return this.commandBase;
    }

    @Override
    public ConfigStore getConfigStore() {
        return this.configStore;
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
