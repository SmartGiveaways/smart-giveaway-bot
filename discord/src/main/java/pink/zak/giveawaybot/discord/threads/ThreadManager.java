package pink.zak.giveawaybot.discord.threads;

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
        this.threadPools.put(ThreadFunction.STORAGE, Executors.newFixedThreadPool(5, this.getThreadFactory("storage")));
        this.threadPools.put(ThreadFunction.GENERAL, Executors.newFixedThreadPool(5, this.getThreadFactory("commands")));
        this.threadPools.put(ThreadFunction.SCHEDULERS, Executors.newScheduledThreadPool(4, this.getThreadFactory("scheduling")));
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

    private ThreadFactory getThreadFactory(String name) {
        return new ThreadFactoryBuilder().setNameFormat(name.concat("-%d")).build();
    }
}
