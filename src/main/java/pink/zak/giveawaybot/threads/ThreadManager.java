package pink.zak.giveawaybot.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public interface ThreadManager {

    void shutdownPools();

    ExecutorService getAsyncExecutor(ThreadFunction function);

    default void runAsync(ThreadFunction function, Runnable runnable) {
        this.getAsyncExecutor(function).execute(runnable);
    }

    default ScheduledExecutorService getScheduler() {
        return (ScheduledExecutorService) this.getAsyncExecutor(ThreadFunction.SCHEDULERS);
    }
}
