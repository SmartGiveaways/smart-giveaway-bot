package pink.zak.giveawaybot.discord.service.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.command.console.ConsoleCommandBase;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleBaseCommand;
import pink.zak.giveawaybot.discord.service.command.discord.DiscordCommandBase;
import pink.zak.giveawaybot.discord.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.discord.service.config.Config;
import pink.zak.giveawaybot.discord.service.config.ConfigStore;
import pink.zak.giveawaybot.discord.service.registry.Registry;
import pink.zak.giveawaybot.discord.service.storage.BackendFactory;
import pink.zak.giveawaybot.discord.service.storage.settings.StorageSettings;

import java.nio.file.Path;
import java.util.Set;
import java.util.function.UnaryOperator;

public interface SimpleBot {

    void unload();

    void onConnect();

    void buildJdaEarly(String token, Set<GatewayIntent> intents, UnaryOperator<DefaultShardManagerBuilder> jdaOperator);

    void initialize(GiveawayBot bot, String token, String prefix, Set<GatewayIntent> intents, UnaryOperator<DefaultShardManagerBuilder> jdaOperator);

    void initialize(GiveawayBot bot, String token, String prefix, Set<GatewayIntent> intents);

    void registerRegistries(Registry... registries);

    void registerCommands(@NotNull SimpleCommand... commands);

    void registerConsoleCommands(@NotNull ConsoleBaseCommand... commands);

    void registerListeners(@NotNull Object... listeners);

    void unRegisterListeners(@NotNull Object... listeners);

    boolean isConnected();

    void setConnected(boolean connected);

    boolean isInitialized();

    StorageSettings getStorageSettings();

    BackendFactory getBackendFactory();

    Path getBasePath();

    String getPrefix();

    DiscordCommandBase getDiscordCommandBase();

    ConsoleCommandBase getConsoleCommandBase();

    ConfigStore getConfigStore();

    Config getConfig(String name);

    ShardManager getShardManager();

    JDA getJda();
}
