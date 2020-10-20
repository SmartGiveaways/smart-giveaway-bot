package pink.zak.giveawaybot.controller;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.models.User;

import java.util.concurrent.CompletableFuture;

public class UserController {
    private final ServerCache serverCache;

    public UserController(GiveawayBot bot) {
        this.serverCache = bot.getServerCache();
    }

    public CompletableFuture<User> getUser(long serverId, long userId) {
        return this.serverCache.get(serverId).thenApply(server -> {
            return server.getUserCache().getSync(userId);
        }).exceptionally(ex -> {
            GiveawayBot.getLogger().error("", ex);
            return null;
        });
    }
}
