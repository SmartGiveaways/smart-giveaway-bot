package pink.zak.giveawaybot.service.cache.caches;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.cache.options.CacheExpiryListener;
import pink.zak.giveawaybot.service.cache.options.CacheStorage;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class AccessExpiringCache<K, V> extends Cache<K, V> {
    protected final Map<K, Long> accessTimes = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler;
    private final CacheExpiryListener<K, V> expiryListener;
    private final TimeUnit timeUnit;
    private final int delay;

    public AccessExpiringCache(GiveawayBot bot, CacheStorage<K, V> storage, CacheExpiryListener<K, V> expiryListener, Consumer<V> removalAction, TimeUnit timeUnit, int delay, TimeUnit autoSaveUnit, int autoSaveInterval) {
        super(bot, removalAction, storage, autoSaveUnit, autoSaveInterval);
        this.scheduler = bot.getThreadManager().getScheduler();
        this.expiryListener = expiryListener;
        this.timeUnit = timeUnit;
        this.delay = delay;

        this.startScheduledCleanup();
    }

    public AccessExpiringCache(GiveawayBot bot, CacheStorage<K, V> storage, CacheExpiryListener<K, V> expiryListener, Consumer<V> removalAction, TimeUnit timeUnit, int delay) {
        this(bot, storage, expiryListener, removalAction, timeUnit, delay, null, 0);
    }

    public AccessExpiringCache(GiveawayBot bot, CacheStorage<K, V> storage, TimeUnit timeUnit, int delay, TimeUnit autoSaveUnit, int autoSaveInterval) {
        this(bot, storage, null, null, timeUnit, delay, autoSaveUnit, autoSaveInterval);
    }

    public AccessExpiringCache(GiveawayBot bot, CacheStorage<K, V> storage, TimeUnit timeUnit, int delay) {
        this(bot, storage, null, null, timeUnit, delay);
    }

    @Override
    public V getSync(K key) {
        V retrieved = super.getSync(key);
        if (retrieved != null) {
            this.accessTimes.put(key, System.currentTimeMillis());
        }
        return retrieved;
    }

    @Override
    public CompletableFuture<V> get(K key) {
        return super.get(key).thenApply(retrieved -> {
            if (retrieved != null) {
                this.accessTimes.put(key, System.currentTimeMillis());
            }
            return retrieved;
        }).exceptionally(ex -> {
            GiveawayBot.logger().error("", ex);
            return null;
        });
    }

    @Override
    public V invalidate(K key) {
        this.accessTimes.remove(key);
        if (this.expiryListener != null) {
            this.expiryListener.onExpiry(key, this.getSync(key));
        }
        return super.invalidate(key);
    }

    @Override
    public V invalidate(K key, boolean save) {
        this.accessTimes.remove(key);
        if (save && this.expiryListener != null) {
            this.expiryListener.onExpiry(key, this.getSync(key));
        }
        return super.invalidate(key, save);
    }

    @Override
    public void invalidateAll() {
        this.accessTimes.clear();
        super.invalidateAll();
    }

    @Override
    public CompletableFuture<Void> invalidateAllAsync() {
        this.accessTimes.clear();
        return super.invalidateAllAsync();
    }

    @Override
    public CompletableFuture<Void> invalidateEachAsync() {
        this.accessTimes.clear();
        return super.invalidateEachAsync();
    }

    private void startScheduledCleanup() {
        this.scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            for (Map.Entry<K, Long> entry : this.accessTimes.entrySet()) {
                if (currentTime - entry.getValue() > this.timeUnit.toMillis(this.delay)) {
                    this.invalidate(entry.getKey());
                }
            }
        }, this.delay, this.delay, this.timeUnit);
    }
}
