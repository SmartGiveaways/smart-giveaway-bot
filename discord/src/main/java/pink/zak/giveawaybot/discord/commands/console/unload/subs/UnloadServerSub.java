package pink.zak.giveawaybot.discord.commands.console.unload.subs;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.cache.ServerCache;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleSubCommand;

import java.util.List;

public class UnloadServerSub extends ConsoleSubCommand {
    private final ServerCache serverCache;

    public UnloadServerSub(GiveawayBot bot) {
        super(bot, false);
        this.serverCache = bot.getServerCache();

        this.addFlat("server");
        this.addArgument(Long.class); // server id
    }

    @Override
    public void onExecute(List<String> args) {
        long serverId = this.parseArgument(args, 1);
        if (serverId == -1) {
            JdaBot.logger.error("Input is not a long ({})", args.get(1));
            return;
        }
        if (!this.serverCache.contains(serverId)) {
            JdaBot.logger.error("The server {} is not cached", serverId);
            return;
        }
        JdaBot.logger.warn("Invalidating {}", serverId);
        this.serverCache.invalidate(serverId);
    }
}
