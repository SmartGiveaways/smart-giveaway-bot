package pink.zak.giveawaybot.service.set;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WriteExpiringSet<V> {
    private final Map<V, Long> items = Maps.newConcurrentMap();
    private final long delayMillis;

    public WriteExpiringSet(ScheduledExecutorService scheduledExecutor, TimeUnit timeUnit, long delay) {
        this.delayMillis = timeUnit.toMillis(delay);
        this.startScheduledCleanup(scheduledExecutor);
    }

    public void add(V item) {
        this.items.put(item, System.currentTimeMillis());
    }

    public void remove(V item) {
        this.items.remove(item);
    }

    public boolean contains(V item) {
        return this.items.containsKey(item) && (System.currentTimeMillis() - this.items.get(item)) < this.delayMillis;
    }

    private void startScheduledCleanup(ScheduledExecutorService scheduledExecutor) {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            for (Map.Entry<V, Long> entry : this.items.entrySet()) {
                if (currentTime - entry.getValue() > this.delayMillis) {
                    this.items.remove(entry.getKey());
                }
            }
        }, this.delayMillis, this.delayMillis, TimeUnit.MILLISECONDS);
    }
}
