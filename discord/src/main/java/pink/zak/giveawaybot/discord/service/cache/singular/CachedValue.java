package pink.zak.giveawaybot.discord.service.cache.singular;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CachedValue<V> {
    private final Supplier<V> valueSupplier;
    private final TimeUnit timeUnit;
    private final int delay;
    private V value;
    private long lastUpdateTime;

    public CachedValue(TimeUnit timeUnit, int delay, Supplier<V> valueSupplier) {
        this.valueSupplier = valueSupplier;
        this.updateAndGet();
        this.timeUnit = timeUnit;
        this.delay = delay;
    }

    public V get() {
        if (System.currentTimeMillis() - this.lastUpdateTime > this.timeUnit.toMillis(delay)) {
            return this.updateAndGet();
        }
        return this.value;
    }

    public V updateAndGet() {
        this.value = this.valueSupplier.get();
        this.lastUpdateTime = System.currentTimeMillis();
        return this.value;
    }

    public V getAndUpdate() {
        V initialValue = this.value;
        this.value = this.valueSupplier.get();
        this.lastUpdateTime = System.currentTimeMillis();
        return initialValue;
    }

    public V getWithoutUpdating() {
        return this.value;
    }
}
