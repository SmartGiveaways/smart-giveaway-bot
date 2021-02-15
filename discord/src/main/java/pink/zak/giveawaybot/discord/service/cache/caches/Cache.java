package pink.zak.giveawaybot.discord.service.cache.caches;

import pink.zak.giveawaybot.discord.service.cache.caches.shutdown.ShutdownData;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoStorage;
import pink.zak.giveawaybot.discord.threads.ThreadFunction;
import pink.zak.giveawaybot.discord.threads.ThreadManager;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Cache<K, V> {
    protected final ConcurrentHashMap<K, V> cacheMap = new ConcurrentHashMap<>();

    protected final ThreadManager threadManager;
    protected final MongoStorage<K, V> storage;
    protected final AtomicInteger hits = new AtomicInteger();
    protected final AtomicInteger loads = new AtomicInteger();

    protected ShutdownData<K, V> shutdownData;

    public Cache(ThreadManager threadManager) {
        this(threadManager, null);
    }

    public Cache(ThreadManager threadManager, MongoStorage<K, V> storage) {
        this(threadManager, storage, null, 0);
    }

    public Cache(ThreadManager threadManager, MongoStorage<K, V> storage, TimeUnit autoSaveTimeUnit, int autoSaveInterval) {
        this.threadManager = threadManager;
        this.storage = storage;
        if (autoSaveTimeUnit != null && autoSaveInterval > 0) {
            this.startAutoSave(threadManager.getScheduler(), autoSaveTimeUnit, autoSaveInterval);
        }
    }

    public V get(K key) {
        V retrieved = this.cacheMap.get(key);
        this.hits.incrementAndGet();
        if (retrieved == null) {
            if (this.storage == null) {
                return null;
            }
            V loaded = this.storage.load(key).join();
            this.loads.incrementAndGet();
            if (loaded != null) {
                return this.set(key, loaded);
            }
            return null;
        }
        return retrieved;
    }

    public CompletableFuture<V> getAsync(K key, ThreadFunction threadFunction) {
        return CompletableFuture.supplyAsync(() -> this.get(key), this.threadManager.getAsyncExecutor(threadFunction));
    }

    public V set(K key, V value) {
        this.cacheMap.put(key, value);
        return value;
    }

    public boolean contains(K key) {
        return this.cacheMap.containsKey(key);
    }

    public void save(K key) {
        this.storage.save(this.get(key));
    }

    public V invalidate(K key) {
        if (this.storage != null) {
            this.save(key);
        }
        return this.cacheMap.remove(key);
    }

    public V invalidate(K key, boolean save) {
        if (save) {
            this.invalidate(key);
            return null;
        }
        return this.cacheMap.remove(key);
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

    public CompletableFuture<Void> invalidateAllAsync(ThreadFunction threadFunction) {
        return CompletableFuture.supplyAsync(() -> {
            this.invalidateAll();
            return null;
        }, this.threadManager.getAsyncExecutor(threadFunction));
    }

    public Set<CompletableFuture<Void>> shutdown() {
        this.shutdownData = new ShutdownData<>(this);
        Set<CompletableFuture<Void>> futures = this.cacheMap.values().stream().map(this::shutdownSave).collect(Collectors.toSet());
        this.cacheMap.clear();
        return futures;
    }

    private CompletableFuture<Void> shutdownSave(V value) {
        return this.storage.save(value).whenComplete((u, ex) -> this.shutdownData.getSaved().incrementAndGet());
    }

    public ShutdownData<K, V> getShutdownData() {
        return this.shutdownData;
    }

    public int size() {
        return this.cacheMap.size();
    }

    public ConcurrentMap<K, V> getMap() {
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

    public MongoStorage<K, V> getStorage() {
        return this.storage;
    }

    private void startAutoSave(ScheduledExecutorService scheduler, TimeUnit timeUnit, int interval) {
        scheduler.scheduleAtFixedRate(() -> {
            this.threadManager.getAsyncExecutor(ThreadFunction.STORAGE).submit(() -> {
                for (K key : this.cacheMap.keySet()) {
                    this.save(key);
                }
            });
        }, interval, interval, timeUnit);
    }
}
