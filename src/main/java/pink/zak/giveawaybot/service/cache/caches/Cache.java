package pink.zak.giveawaybot.service.cache.caches;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.cache.options.CacheStorage;
import pink.zak.giveawaybot.threads.ThreadFunction;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Cache<K, V> {
    protected final ConcurrentHashMap<K, V> cacheMap = new ConcurrentHashMap<>();

    protected final ExecutorService executor;
    protected final Consumer<V> removalAction;
    protected final CacheStorage<K, V> storage;
    protected final AtomicInteger hits = new AtomicInteger();
    protected final AtomicInteger loads = new AtomicInteger();

    public Cache(GiveawayBot bot) {
        this(bot, null, null);
    }

    public Cache(GiveawayBot bot, Consumer<V> removalAction, CacheStorage<K, V> storage) {
        this(bot, removalAction, storage, null, 0);
    }

    public Cache(GiveawayBot bot, Consumer<V> removalAction, CacheStorage<K, V> storage, TimeUnit autoSaveTimeUnit, int autoSaveInterval) {
        this.executor = bot.getAsyncExecutor(ThreadFunction.STORAGE);
        this.removalAction = removalAction;
        this.storage = storage;
        if (autoSaveTimeUnit != null && autoSaveInterval > 0) {
            this.startAutoSave(bot.getThreadManager().getScheduler(), autoSaveTimeUnit, autoSaveInterval);
        }
    }

    public V getSync(K key) {
        V retrieved = this.cacheMap.get(key);
        this.hits.incrementAndGet();
        if (retrieved == null) {
            if (this.storage == null) {
                return null;
            }
            V loaded = this.storage.load(key);
            this.loads.incrementAndGet();
            if (loaded != null) {
                return this.setSync(key, loaded);
            }
            return null;
        }
        return retrieved;
    }

    public CompletableFuture<V> get(K key) {
        return CompletableFuture.supplyAsync(() -> this.getSync(key), this.executor);
    }

    public V setSync(K key, V value) {
        this.cacheMap.put(key, value);
        return value;
    }

    public CompletableFuture<V> set(K key, V value) {
        return CompletableFuture.supplyAsync(() -> this.setSync(key, value), this.executor);
    }

    public boolean contains(K key) {
        return this.cacheMap.containsKey(key);
    }

    public void save(K key) {
        this.storage.save(this.getSync(key));
    }

    public void invalidate(K key) {
        if (this.storage != null) {
            this.save(key);
        }
        if (this.removalAction != null) {
            this.removalAction.accept(this.cacheMap.get(key));
        }
        this.cacheMap.remove(key);
    }

    public void invalidateAsync(K key) {
        CompletableFuture.runAsync(() -> this.invalidate(key), this.executor);
    }

    public void invalidate(K key, boolean save) {
        if (save) {
            this.invalidate(key);
            return;
        }
        this.cacheMap.remove(key);
    }

    public void invalidateAsync(K key, boolean save) {
        CompletableFuture.runAsync(() -> this.invalidate(key, save), this.executor);
    }

    public void invalidateAll() {
        if (this.storage == null) {
            this.cacheMap.clear();
            return;
        }
        for (K key : this.cacheMap.keySet()) {
            this.invalidate(key);
        }
    }

    public void invalidateAllAsync() {
        CompletableFuture.runAsync(this::invalidateAll, this.executor);
    }

    public int size() {
        return this.cacheMap.size();
    }

    public ConcurrentHashMap<K, V> getMap() {
        return this.cacheMap;
    }

    public Cache<K, V> getCache() {
        return this;
    }

    public AtomicInteger getHits() {
        return this.hits;
    }

    public void resetHits() {
        this.hits.set(0);
    }

    public AtomicInteger getLoads() {
        return this.loads;
    }

    public void resetLoads() {
        this.loads.set(0);
    }

    private void startAutoSave(ScheduledExecutorService scheduledExecutor, TimeUnit timeUnit, int interval) {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            this.executor.submit(() -> {
                for (K key : this.cacheMap.keySet()) {
                    this.save(key);
                }
            });
        }, interval, interval, timeUnit);
    }
}
