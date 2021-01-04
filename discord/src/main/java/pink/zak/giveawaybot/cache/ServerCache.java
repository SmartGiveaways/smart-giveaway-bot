package pink.zak.giveawaybot.cache;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.cache.caches.AccessExpiringCache;

import java.util.concurrent.TimeUnit;

public class ServerCache extends AccessExpiringCache<Long, Server> {
    public static ServerCache apiInstance;

    public ServerCache(GiveawayBot bot) {
        super(bot, bot.getServerStorage(), null, server -> server.getUserCache().invalidateAll(), TimeUnit.MINUTES, 10, TimeUnit.MINUTES, 5);
        apiInstance = this;
    }

    public void shutdown() {
        /*for (Server server : this.getMap().values()) {
            server.getUserCache().invalidateAll();
        }*/ // In theory, this shouldn't be required due to the removal action
        this.invalidateAll();
    }
}
