package pink.zak.giveawaybot.service.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.command.CommandBase;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;
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

    void initialize(GiveawayBot bot, String token, String prefix, Set<GatewayIntent> intents, UnaryOperator<DefaultShardManagerBuilder> jdaOperator);

    void initialize(GiveawayBot bot, String token, String prefix, Set<GatewayIntent> intents);

    void registerRegistries(Registry... registries);

    void registerCommands(SimpleCommand... commands);

    void registerListeners(Object... listeners);

    boolean isInitialized();

    StorageSettings getStorageSettings();

    BackendFactory getBackendFactory();

    Path getBasePath();

    String getPrefix();

    CommandBase getCommandBase();

    ConfigStore getConfigStore();

    ShardManager getShardManager();

    JDA getJda();
}
