package pink.zak.giveawaybot.discord.service.cache.caches;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.cache.options.CacheExpiryListener;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoStorage;
import pink.zak.giveawaybot.discord.threads.ThreadFunction;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class WriteExpiringCache<K, V> extends Cache<K, V> {
    protected final Map<K, Long> expiryTimes = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler;
    private final CacheExpiryListener<K, V> expiryListener;
    private final long delayMillis;

    public WriteExpiringCache(GiveawayBot bot, MongoStorage<K, V> storage, CacheExpiryListener<K, V> expiryListener, Consumer<V> removalAction, TimeUnit timeUnit, int delay, TimeUnit autoSaveUnit, int autoSaveInterval) {
        super(bot, removalAction, storage, autoSaveUnit, autoSaveInterval);
        this.scheduler = bot.getThreadManager().getScheduler();
        this.expiryListener = expiryListener;
        this.delayMillis = timeUnit.toMillis(delay);

        this.startScheduledCleanup();
    }

    public WriteExpiringCache(GiveawayBot bot, MongoStorage<K, V> storage, CacheExpiryListener<K, V> expiryListener, Consumer<V> removalAction, TimeUnit timeUnit, int delay) {
        this(bot, storage, expiryListener, removalAction, timeUnit, delay, null, 0);
    }

    public WriteExpiringCache(GiveawayBot bot, MongoStorage<K, V> storage, TimeUnit timeUnit, int delay, TimeUnit autoSaveUnit, int autoSaveInterval) {
        this(bot, storage, null, null, timeUnit, delay, autoSaveUnit, autoSaveInterval);
    }

    public WriteExpiringCache(GiveawayBot bot, MongoStorage<K, V> storage, TimeUnit timeUnit, int delay) {
        this(bot, storage, null, null, timeUnit, delay);
    }

    @Override
    public V set(K key, V value) {
        this.expiryTimes.put(key, System.currentTimeMillis() + this.delayMillis);
        return super.set(key, value);
    }

    @Override
    public V get(K key) {
        V retrieved = super.get(key);
        return this.getAndCheck(key, retrieved);
    }

    @Override
    public CompletableFuture<V> getAsync(K key, ThreadFunction threadFunction) {
        return super.getAsync(key, threadFunction).thenApply(retrieved -> this.getAndCheck(key, retrieved))
                .exceptionally(ex -> {
                    GiveawayBot.logger().error("", ex);
                    return null;
                });
    }

    private V getAndCheck(K key, V retrieved) {
        if (retrieved == null) {
            return null;
        }
        if (this.expiryTimes.get(key) <= System.currentTimeMillis()) {
            this.invalidate(key);
            return null;
        }
        return retrieved;
    }

    @Override
    public V invalidate(K key) {
        this.expiryTimes.remove(key);
        if (this.expiryListener != null) {
            this.expiryListener.onExpiry(key, this.get(key));
        }
        return super.invalidate(key);
    }

    @Override
    public V invalidate(K key, boolean save) {
        this.expiryTimes.remove(key);
        if (save && this.expiryListener != null) {
            this.expiryListener.onExpiry(key, this.get(key));
        }
        return super.invalidate(key, save);
    }

    @Override
    public void invalidateAll() {
        this.expiryTimes.clear();
        super.invalidateAll();
    }

    @Override
    public CompletableFuture<Void> invalidateAllAsync(ThreadFunction threadFunction) {
        this.expiryTimes.clear();
        return super.invalidateAllAsync(threadFunction);
    }

    @Override
    public Set<CompletableFuture<Void>> shutdown() {
        this.expiryTimes.clear();
        return super.shutdown();
    }

    @Override
    public boolean contains(K key) {
        return super.contains(key) && this.expiryTimes.containsKey(key) && System.currentTimeMillis() < this.expiryTimes.get(key);
    }

    private void startScheduledCleanup() {
        this.scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            for (Map.Entry<K, Long> entry : this.expiryTimes.entrySet()) {
                if (currentTime > entry.getValue()) {
                    this.invalidate(entry.getKey());
                }
            }
        }, 5, 5, TimeUnit.MINUTES);
    }
}
