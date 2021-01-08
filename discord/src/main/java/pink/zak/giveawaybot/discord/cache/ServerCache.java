package pink.zak.giveawaybot.discord.cache;

import com.google.common.collect.Sets;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.service.cache.caches.AccessExpiringCache;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ServerCache extends AccessExpiringCache<Long, Server> {

    public ServerCache(GiveawayBot bot) {
        super(bot, bot.getServerStorage(), null, server -> server.getUserCache().invalidateAll(), TimeUnit.MINUTES, 10, TimeUnit.MINUTES, 5);
    }

    public Set<CompletableFuture<Void>> shutdown() {
        Set<CompletableFuture<Void>> futures = Sets.newHashSet();
        this.cacheMap.values().stream().map(Server::getUserCache).map(UserCache::shutdown).forEach(futures::addAll);

        futures.addAll(super.shutdown());
        return futures;
    }

    public Server getOrInitialise(Long key, String languageId) {
        if (super.contains(key)) {
            return super.get(key);
        }
        Server server = super.storage.create(key);
        super.set(key, server);
        server.setLanguage(languageId);
        return super.get(key);
    }
}
