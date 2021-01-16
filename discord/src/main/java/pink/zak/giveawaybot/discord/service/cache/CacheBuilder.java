package pink.zak.giveawaybot.discord.service.cache;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.cache.caches.AccessExpiringCache;
import pink.zak.giveawaybot.discord.service.cache.caches.Cache;
import pink.zak.giveawaybot.discord.service.cache.caches.WriteExpiringCache;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoStorage;

import java.util.concurrent.TimeUnit;

public class CacheBuilder<K, V> {
    private GiveawayBot bot;
    private MongoStorage<K, V> storage;
    private TimeUnit autoSaveTimeUnit;
    private int autoSaveInterval;
    private TimeUnit expiryTimeUnit;
    private int expiryDelay;
    private boolean expireAfterAccess;

    public Cache<K, V> build() {
        if (this.expiryTimeUnit != null && this.expiryDelay > 0) {
            if (this.expireAfterAccess) {
                return new AccessExpiringCache<>(this.bot, this.storage, this.expiryTimeUnit, this.expiryDelay, this.autoSaveTimeUnit, this.autoSaveInterval);
            }
            return new WriteExpiringCache<>(this.bot, this.storage, this.expiryTimeUnit, this.expiryDelay, this.autoSaveTimeUnit, this.autoSaveInterval);
        }
        return new Cache<>(this.bot, this.storage, this.autoSaveTimeUnit, this.autoSaveInterval);
    }

    public CacheBuilder<K, V> setControlling(GiveawayBot bot) {
        this.bot = bot;
        return this;
    }

    public CacheBuilder<K, V> expireAfterAccess(int delay, TimeUnit timeUnit) {
        this.expiryTimeUnit = timeUnit;
        this.expiryDelay = delay;
        this.expireAfterAccess = true;
        return this;
    }

    public CacheBuilder<K, V> expireAfterWrite(int delay, TimeUnit timeUnit) {
        this.expiryTimeUnit = timeUnit;
        this.expiryDelay = delay;
        this.expireAfterAccess = false;
        return this;
    }

    public CacheBuilder<K, V> autoSave(int delay, TimeUnit timeUnit) {
        this.autoSaveTimeUnit = timeUnit;
        this.autoSaveInterval = delay;
        return this;
    }

    public CacheBuilder<K, V> setStorage(MongoStorage<K, V> storage) {
        this.storage = storage;
        return this;
    }
}
