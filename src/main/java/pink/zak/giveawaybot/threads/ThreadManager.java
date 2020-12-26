package pink.zak.giveawaybot.threads;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Map;
import java.util.concurrent.*;

public class ThreadManager {
    private final Map<ThreadFunction, ExecutorService> threadPools = Maps.newEnumMap(ThreadFunction.class);

    public ThreadManager() {
        this.initiatePools();
    }

    public void runAsync(ThreadFunction function, Runnable runnable) {
        this.threadPools.get(function).submit(runnable);
    }

    private void initiatePools() {
        this.threadPools.put(ThreadFunction.STORAGE, Executors.newFixedThreadPool(2, getThreadFactory("storage")));
        this.threadPools.put(ThreadFunction.GENERAL, Executors.newFixedThreadPool(2, getThreadFactory("commands")));
        this.threadPools.put(ThreadFunction.SCHEDULERS, Executors.newScheduledThreadPool(2, getThreadFactory("scheduling")));
    }

    public void shutdownPools() {
        for (ExecutorService executorService : this.threadPools.values()) {
            executorService.shutdown();
        }
    }

    public ExecutorService getAsyncExecutor(ThreadFunction function) {
        return this.threadPools.get(function);
    }

    public ScheduledExecutorService getScheduler() {
        return (ScheduledExecutorService) this.threadPools.get(ThreadFunction.SCHEDULERS);
    }

    public static ThreadFactory getThreadFactory(String name) {
        return new ThreadFactoryBuilder().setNameFormat(name.concat("-%d")).build();
    }
}
