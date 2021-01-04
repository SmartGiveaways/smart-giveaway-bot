package pink.zak.giveawaybot.api.controllers;

import org.springframework.stereotype.Component;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.User;

@Component
public class ServerControllerImpl implements ServerController {
    private final ServerCache serverCache = ServerCache.apiInstance;

    @Override
    public Server getServer(long id) {
        return this.serverCache.getSync(id);
    }

    @Override
    public User getUser(long serverId, long userId) {
        return this.serverCache.getSync(serverId).getUserCache().getSync(userId);
    }
}
