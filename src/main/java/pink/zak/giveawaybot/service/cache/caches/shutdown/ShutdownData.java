package pink.zak.giveawaybot.service.cache.caches.shutdown;

import pink.zak.giveawaybot.service.cache.caches.Cache;

import java.util.concurrent.atomic.AtomicInteger;

public class ShutdownData<K, V> {
    private final long startTime;
    private final int size;
    private final AtomicInteger saved = new AtomicInteger();

    public ShutdownData(Cache<K, V> cache) {
        this.startTime = System.currentTimeMillis();
        this.size = cache.size();
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getTimeSinceStart() {
        return System.currentTimeMillis() - this.startTime;
    }

    public int getSize() {
        return this.size;
    }

    public AtomicInteger getSaved() {
        return this.saved;
    }

    public int getCompletionPercentage() {
        return (this.saved.get() / this.size ) * 100;
    }
}
