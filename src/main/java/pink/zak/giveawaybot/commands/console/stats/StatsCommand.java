package pink.zak.giveawaybot.commands.console.stats;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.console.stats.subs.StatsServerSub;
import pink.zak.giveawaybot.data.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.data.cache.GiveawayCache;
import pink.zak.giveawaybot.data.cache.ServerCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.command.console.command.ConsoleCommand;

import java.util.List;

public class StatsCommand extends ConsoleCommand {
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
            loadedUsers += server.getUserCache().size();
        }
        JdaBot.LOGGER.info("stats <server-id>\n");
        JdaBot.LOGGER.info("Loaded Users: {}", loadedUsers);
        JdaBot.LOGGER.info("Loaded Servers: {}", loadedServers);
        JdaBot.LOGGER.info("Current Giveaways: {}", currentGiveaways);
        JdaBot.LOGGER.info("Loaded Finished Giveaways: {}", loadedFinishedGiveaways);
    }
}
