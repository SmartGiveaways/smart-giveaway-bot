package pink.zak.giveawaybot.service.cache.caches;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.storage.mongo.MongoStorage;
import pink.zak.giveawaybot.threads.ThreadFunction;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AccessExpiringCache<K, V> extends Cache<K, V> {
    protected final Map<K, Long> accessTimes = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler;
    private final TimeUnit timeUnit;
    private final int delay;

    public AccessExpiringCache(GiveawayBot bot, MongoStorage<K, V> storage, TimeUnit timeUnit, int delay, TimeUnit autoSaveUnit, int autoSaveInterval) {
        super(bot.getThreadManager(), storage, autoSaveUnit, autoSaveInterval);
        this.scheduler = bot.getThreadManager().getScheduler();
        this.timeUnit = timeUnit;
        this.delay = delay;

        this.startScheduledCleanup();
    }

    public AccessExpiringCache(GiveawayBot bot, MongoStorage<K, V> storage, TimeUnit timeUnit, int delay) {
        this(bot, storage, timeUnit, delay, null, 0);
    }

    @Override
    public V get(K key) {
        V retrieved = super.get(key);
        if (retrieved != null) {
            this.accessTimes.put(key, System.currentTimeMillis());
        }
        return retrieved;
    }

    @Override
    public CompletableFuture<V> getAsync(K key, ThreadFunction threadFunction) {
        return super.getAsync(key, threadFunction).thenApply(retrieved -> {
            if (retrieved != null) {
                this.accessTimes.put(key, System.currentTimeMillis());
            }
            return retrieved;
        }).exceptionally(ex -> {
            JdaBot.LOGGER.error("", ex);
            return null;
        });
    }

    @Override
    public V invalidate(K key) {
        this.accessTimes.remove(key);
        return super.invalidate(key);
    }

    @Override
    public V invalidate(K key, boolean save) {
        this.accessTimes.remove(key);
        return super.invalidate(key, save);
    }

    @Override
    public void invalidateAll() {
        this.accessTimes.clear();
        super.invalidateAll();
    }

    @Override
    public CompletableFuture<Void> invalidateAllAsync(ThreadFunction threadFunction) {
        this.accessTimes.clear();
        return super.invalidateAllAsync(threadFunction);
    }

    @Override
    public Set<CompletableFuture<Void>> shutdown() {
        this.accessTimes.clear();
        return super.shutdown();
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
