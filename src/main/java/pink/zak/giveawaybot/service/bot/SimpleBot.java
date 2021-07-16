package pink.zak.giveawaybot.service.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.command.console.ConsoleCommandBase;
import pink.zak.giveawaybot.service.command.console.command.ConsoleBaseCommand;
import pink.zak.giveawaybot.service.command.discord.DiscordCommandBase;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.service.config.Config;
import pink.zak.giveawaybot.service.config.ConfigStore;
import pink.zak.giveawaybot.service.registry.Registry;
import pink.zak.giveawaybot.service.storage.BackendFactory;
import pink.zak.giveawaybot.service.storage.settings.StorageSettings;

import java.nio.file.Path;
import java.util.Set;
import java.util.function.UnaryOperator;

public interface SimpleBot {

    void unload();

    void onConnect();

    void buildJdaEarly(String token, Set<GatewayIntent> intents, UnaryOperator<DefaultShardManagerBuilder> jdaOperator);

    void initialize(GiveawayBot bot, String token, Set<GatewayIntent> intents, UnaryOperator<DefaultShardManagerBuilder> jdaOperator);

    void initialize(GiveawayBot bot, String token, Set<GatewayIntent> intents);

    void registerRegistries(Registry... registries);

    void registerCommands(@NotNull SimpleCommand... commands);

    void registerConsoleCommands(@NotNull ConsoleBaseCommand... commands);

    void registerListeners(@NotNull Object... listeners);

    void deregisterListeners(@NotNull Object... listeners);

    boolean isConnected();

    void setConnected(boolean connected);

    boolean isInitialized();

    StorageSettings getStorageSettings();

    BackendFactory getBackendFactory();

    Path getBasePath();

    DiscordCommandBase getDiscordCommandBase();

    ConsoleCommandBase getConsoleCommandBase();

    ConfigStore getConfigStore();

    Config getConfig(String name);

    ShardManager getShardManager();

    JDA getJda();
}
