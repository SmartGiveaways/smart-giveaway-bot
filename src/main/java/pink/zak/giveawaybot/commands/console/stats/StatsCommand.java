package pink.zak.giveawaybot.commands.console.stats;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.commands.console.stats.subs.StatsServerSub;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.console.command.ConsoleBaseCommand;

import java.util.List;

public class StatsCommand extends ConsoleBaseCommand {
    private final ServerCache serverCache;
    private final GiveawayCache giveawayCache;
    private final FinishedGiveawayCache finishedGiveawayCache;

    public StatsCommand(GiveawayBot bot) {
        super(bot, "stats");
        this.serverCache = bot.getServerCache();
        this.giveawayCache = bot.getGiveawayCache();
        this.finishedGiveawayCache = bot.getFinishedGiveawayCache();

        this.setSubCommands(
                new StatsServerSub(bot)
        );
    }

    @Override
    public void onExecute(List<String> args) {
        int loadedUsers = 0;
        int loadedServers = this.serverCache.size();
        int currentGiveaways = this.giveawayCache.size();
        int loadedFinishedGiveaways = this.finishedGiveawayCache.size();
        for (Server server : this.serverCache.getMap().values()) {
            loadedUsers += server.userCache().size();
        }
        GiveawayBot.logger().info("stats <server-id>\n");
        GiveawayBot.logger().info("Loaded Users: {}", loadedUsers);
        GiveawayBot.logger().info("Loaded Servers: {}", loadedServers);
        GiveawayBot.logger().info("Current Giveaways: {}", currentGiveaways);
        GiveawayBot.logger().info("Loaded Finished Giveaways: {}", loadedFinishedGiveaways);
    }
}
