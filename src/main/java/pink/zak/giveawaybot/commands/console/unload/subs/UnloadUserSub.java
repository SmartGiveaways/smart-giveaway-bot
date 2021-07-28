package pink.zak.giveawaybot.commands.console.unload.subs;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.cache.ServerCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.command.console.command.ConsoleSubCommand;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UnloadUserSub extends ConsoleSubCommand {
    private final ServerCache serverCache;

    public UnloadUserSub(GiveawayBot bot) {
        super(bot, bot.getServerCache(), false);
        this.serverCache = bot.getServerCache();

        this.addFlat("user");
        this.addArgument(Long.class); // server id
    }

    @Override
    public void onExecute(List<String> args) {
        long userId = this.parseArgument(args, 1);
        if (userId == -1) {
            JdaBot.LOGGER.error("Input is not a long ({})", args.get(1));
            return;
        }
        Set<Server> servers = this.serverCache.getAll().stream()
            .filter(server -> server.getUserCache().contains(userId))
            .collect(Collectors.toSet());
        if (servers.isEmpty()) {
            JdaBot.LOGGER.error("The user {} is not cached in any servers", userId);
            return;
        }
        JdaBot.LOGGER.warn("Invalidating {} from {} servers", userId, servers.size());

        for (Server server : servers) {
            server.getUserCache().invalidate(userId);
        }
    }
}
