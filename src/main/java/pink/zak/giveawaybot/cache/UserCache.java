package pink.zak.giveawaybot.cache;

import com.google.common.collect.Maps;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.User;
import pink.zak.giveawaybot.service.cache.caches.AccessExpiringCache;
import pink.zak.giveawaybot.service.storage.storage.Storage;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UserCache extends AccessExpiringCache<Long, User> {
    private final Map<String, String> baseValueMap = Maps.newHashMap();

    public UserCache(GiveawayBot bot, Storage<User> storage, long serverId) {
        super(bot, storage, TimeUnit.MINUTES, 10, TimeUnit.MINUTES, 5);

        this.baseValueMap.put("serverId", String.valueOf(serverId));
    }

    @Override
    public CompletableFuture<User> get(Long key) {
        return CompletableFuture.supplyAsync(() -> {
            User retrieved = this.getSync(key);
            if (retrieved != null) {
                super.accessTimes.put(key, System.currentTimeMillis());
            }
            return retrieved;
        }, super.executor);
    }

    @Override
    public User getSync(Long key) {
        return this.getUserSync(key);
    }

    public User getUserSync(long userId) {
        User retrieved = super.cacheMap.get(userId);
        super.hits.incrementAndGet();
        if (retrieved == null) {
            User loaded = this.storage.load(this.getUserValues(userId));
            super.loads.incrementAndGet();
            if (loaded != null) {
                return this.setSync(userId, loaded);
            }
            return null;
        }
        return retrieved;
    }

    @Override
    public void save(Long key) {
        this.storage.save(this.getUserValues(key), this.cacheMap.get(key));
    }

    @Override
    public void invalidateAll() {
        super.accessTimes.clear();
        for (Long key : this.cacheMap.keySet()) {
            this.invalidate(key);
        }
    }

    private Map<String, String> getUserValues(long userId) {
        Map<String, String> userValueMap = Maps.newHashMap(this.baseValueMap);
        userValueMap.put("userId", String.valueOf(userId));
        return userValueMap;
    }
}
