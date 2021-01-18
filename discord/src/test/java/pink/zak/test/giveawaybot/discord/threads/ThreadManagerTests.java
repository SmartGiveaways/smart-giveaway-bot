package pink.zak.test.giveawaybot.discord.threads;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.threads.ThreadFunction;
import pink.zak.giveawaybot.discord.threads.ThreadManager;
import pink.zak.giveawaybot.discord.threads.ThreadManagerImpl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadManagerTests {
    private final ThreadManager threadManager = new ThreadManagerImpl();

    @Test
    void testRunAsync() {
        AtomicBoolean works = new AtomicBoolean(false);
        this.threadManager.runAsync(ThreadFunction.GENERAL, () -> {
            if (Thread.currentThread().getName().startsWith("commands"))
                works.set(true);
        });
        Awaitility.await().atMost(200, TimeUnit.MILLISECONDS).until(works::get);
        assertTrue(works.get());
    }

    @Test
    void testGetScheduler() {
        assertNotNull(this.threadManager.getScheduler());
        assertTrue(CompletableFuture.supplyAsync(() -> true, this.threadManager.getScheduler()).join());
    }

    @Test
    void testShutdownPools() {
        this.threadManager.shutdownPools();
        assertTrue(this.threadManager.getAsyncExecutor(ThreadFunction.GENERAL).isShutdown());
        assertTrue(this.threadManager.getAsyncExecutor(ThreadFunction.STORAGE).isShutdown());
        assertTrue(this.threadManager.getAsyncExecutor(ThreadFunction.SCHEDULERS).isShutdown());
    }
}
