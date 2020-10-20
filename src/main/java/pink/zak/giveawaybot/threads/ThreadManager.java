package pink.zak.giveawaybot.threads;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.concurrent.*;

public class ThreadManager {
    private final Map<ThreadFunction, ExecutorService> threadPools = Maps.newEnumMap(ThreadFunction.class);
    private final Thread mainThread;

    public ThreadManager() {
        this.mainThread = Thread.currentThread();
        this.initiatePools();
    }

    @SneakyThrows
    public void runOnMainThread(Runnable runnable) {
        this.mainThread.join();
        runnable.run();
    }

    public Future<?> runAsync(ThreadFunction function, Runnable runnable) {
        return this.threadPools.get(function).submit(runnable);
    }

    private void initiatePools() {
        this.threadPools.put(ThreadFunction.STORAGE, Executors.newFixedThreadPool(3, this.getThreadFactory("storage")));
        this.threadPools.put(ThreadFunction.COMMANDS, Executors.newFixedThreadPool(3, this.getThreadFactory("commands")));
        this.threadPools.put(ThreadFunction.UPDATING, Executors.newScheduledThreadPool(5, this.getThreadFactory("updating")));
    }

    public void shutdownPools() {
        for (ExecutorService executorService : this.threadPools.values()) {
            executorService.shutdown();
        }
    }

    public ExecutorService getAsyncExecutor(ThreadFunction function) {
        return this.threadPools.get(function);
    }

    public ScheduledExecutorService getUpdaterExecutor() {
        return (ScheduledExecutorService) this.threadPools.get(ThreadFunction.UPDATING);
    }

    private ThreadFactory getThreadFactory(String name) {
        return new ThreadFactoryBuilder().setNameFormat(name.concat("-%d")).build();
    }
}
