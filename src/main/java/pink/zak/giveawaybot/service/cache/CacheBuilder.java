package pink.zak.giveawaybot.service.cache;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.cache.options.CacheExpiryListener;
import pink.zak.giveawaybot.service.storage.storage.Storage;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CacheBuilder<K, V> {
    private GiveawayBot bot;
    private CacheExpiryListener<K, V> expiryListener;
    private Consumer<V> removalAction;
    private Storage<V> storage;
    private TimeUnit autoSaveTimeUnit;
    private int autoSaveInterval;
    private TimeUnit expiryTimeUnit;
    private int expiryDelay;

    public Cache<K, V> build() {
        if (this.expiryTimeUnit != null && this.expiryDelay > 0) {
            return new AccessExpiringCache<>(this.bot, this.storage, this.expiryListener, this.removalAction, this.expiryTimeUnit, this.expiryDelay, this.autoSaveTimeUnit, this.autoSaveInterval);
        }
        return new Cache<>(this.bot, this.removalAction, this.storage, this.autoSaveTimeUnit, this.autoSaveInterval);
    }

    public CacheBuilder<K, V> setControlling(GiveawayBot bot) {
        this.bot = bot;
        return this;
    }

    public CacheBuilder<K, V> setExpiryListener(CacheExpiryListener<K, V> expiryListener) {
        this.expiryListener = expiryListener;
        return this;
    }

    public CacheBuilder<K, V> setRemovalAction(Consumer<V> operator) {
        this.removalAction = operator;
        return this;
    }

    public CacheBuilder<K, V> expireAfterAccess(int delay, TimeUnit timeUnit) {
        this.expiryTimeUnit = timeUnit;
        this.expiryDelay = delay;
        return this;
    }

    public CacheBuilder<K, V> autoSave(int delay, TimeUnit timeUnit) {
        this.autoSaveTimeUnit = timeUnit;
        this.autoSaveInterval = delay;
        return this;
    }

    public CacheBuilder<K, V> setStorage(Storage<V> storage) {
        this.storage = storage;
        return this;
    }
}
