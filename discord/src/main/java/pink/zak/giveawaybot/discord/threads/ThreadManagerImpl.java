package pink.zak.giveawaybot.discord.threads;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManagerImpl implements ThreadManager {
    private final Map<ThreadFunction, ExecutorService> threadPools = Maps.newEnumMap(ThreadFunction.class);

    public ThreadManagerImpl() {
        this.initializePools();
    }

    private void initializePools() {
        this.threadPools.put(ThreadFunction.STORAGE, new ThreadPoolExecutor(5, Integer.MAX_VALUE, 2, TimeUnit.MINUTES, new SynchronousQueue<>(), this.getThreadFactory("storage")));
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

    private ThreadFactory getThreadFactory(String name) {
        return new ThreadFactoryBuilder().setNameFormat(name.concat("-%d")).build();
    }
}
