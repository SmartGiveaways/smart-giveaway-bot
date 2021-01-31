package pink.zak.giveawaybot.discord.commands.console.stats;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.discord.data.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.data.cache.ServerCache;
import pink.zak.giveawaybot.discord.commands.console.stats.subs.StatsServerSub;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleBaseCommand;

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
            loadedUsers += server.getUserCache().size();
        }
        JdaBot.logger.info("stats <server-id>\n");
        JdaBot.logger.info("Loaded Users: {}", loadedUsers);
        JdaBot.logger.info("Loaded Servers: {}", loadedServers);
        JdaBot.logger.info("Current Giveaways: {}", currentGiveaways);
        JdaBot.logger.info("Loaded Finished Giveaways: {}", loadedFinishedGiveaways);
    }
}
