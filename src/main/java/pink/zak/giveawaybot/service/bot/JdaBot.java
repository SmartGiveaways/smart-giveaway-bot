package pink.zak.giveawaybot.service.bot;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.command.CommandBase;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;
import pink.zak.giveawaybot.service.config.ConfigStore;
import pink.zak.giveawaybot.service.console.ConsoleListener;
import pink.zak.giveawaybot.service.storage.BackendFactory;
import pink.zak.giveawaybot.service.storage.settings.StorageSettings;
import pink.zak.simplediscord.registry.Registry;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.UnaryOperator;

public abstract class JdaBot implements SimpleBot {
    protected final StorageSettings storageSettings;
    private final BackendFactory backendFactory;
    private final ConfigStore configStore;
    private final Path basePath;
    private CommandBase commandBase;
    private String prefix;
    private JDA jda;

    @SneakyThrows
    public JdaBot(UnaryOperator<Path> subBasePath) {
        this.storageSettings = new StorageSettings();
        this.backendFactory = new BackendFactory(this);
        this.configStore = new ConfigStore(this);
        System.out.println("Base path set to: ".concat(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().toString()));
        this.basePath = subBasePath.apply(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().toPath().toAbsolutePath());
    }

    @Override
    public abstract void unload();

    @SneakyThrows
    @Override
    public void initialize(GiveawayBot bot, String token, String prefix, Set<GatewayIntent> intents) {
        this.commandBase = new CommandBase(bot);
        this.prefix = prefix;
        try {
            JDABuilder builder = JDABuilder.createDefault(token);
            if (!intents.isEmpty()) {
                builder.enableIntents(intents);
            }
            this.jda = builder.build().awaitReady();
            this.registerListeners(this.commandBase);
        } catch (LoginException e) {
            System.out.println("Unable to log into Discord, the following error occurred:");
            e.printStackTrace();
        }
        new Thread(new ConsoleListener(this)).start();
    }

    @Override
    public void registerRegistries(Registry... registries) {
        for (Registry registry : registries) {
            registry.register();
        }
    }

    @Override
    public void registerCommands(pink.zak.giveawaybot.service.command.command.SimpleCommand... commands) {
        for (SimpleCommand command : commands) {
            this.commandBase.registerCommand(command);
        }
    }

    @Override
    public void registerListeners(Object... listeners) {
        this.jda.addEventListener(listeners);
    }


    @Override
    public boolean isInitialized() {
        return this.jda != null;
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
    public JDA getJda() {
        return this.jda;
    }
}
