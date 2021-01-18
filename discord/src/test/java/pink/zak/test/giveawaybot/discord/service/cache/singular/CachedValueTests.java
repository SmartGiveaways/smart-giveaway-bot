package pink.zak.test.giveawaybot.discord.service.cache.singular;

import lombok.SneakyThrows;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.service.cache.singular.CachedValue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CachedValueTests {

    @Test
    void test() {
        AtomicInteger counter = new AtomicInteger();
        CachedValue<Integer> cachedValue = new CachedValue<>(TimeUnit.MILLISECONDS, 30, counter::incrementAndGet);
        assertEquals(1, cachedValue.get());
        assertEquals(2, cachedValue.updateAndGet());
        assertEquals(2, cachedValue.getAndUpdate());
        Awaitility.await().pollInterval(1, TimeUnit.MILLISECONDS).atMost(40, TimeUnit.MILLISECONDS).until(() -> cachedValue.getWithoutUpdating() == 3);
    }

    @SneakyThrows
    @Test
    void testGetUpdating() {
        AtomicInteger counter = new AtomicInteger();
        CountDownLatch waiter = new CountDownLatch(1);
        CachedValue<Integer> cachedValue = new CachedValue<>(TimeUnit.MILLISECONDS, 2, counter::incrementAndGet);
        waiter.await(2, TimeUnit.MILLISECONDS);
        assertEquals(2, cachedValue.get());
    }
}
