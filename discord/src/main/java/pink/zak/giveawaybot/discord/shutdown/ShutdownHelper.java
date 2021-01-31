package pink.zak.giveawaybot.discord.shutdown;

import com.google.common.collect.Sets;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.discord.data.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.data.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.discord.data.cache.ServerCache;
import pink.zak.giveawaybot.discord.data.cache.UserCache;
import pink.zak.giveawaybot.discord.data.models.Server;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ShutdownHelper {
    private final ServerCache serverCache;
    private final GiveawayCache giveawayCache;
    private final FinishedGiveawayCache finishedGiveawayCache;
    private final ScheduledGiveawayCache scheduledGiveawayCache;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public ShutdownHelper(GiveawayBot bot) {
        this.serverCache = bot.getServerCache();
        this.giveawayCache = bot.getGiveawayCache();
        this.finishedGiveawayCache = bot.getFinishedGiveawayCache();
        this.scheduledGiveawayCache = bot.getScheduledGiveawayCache();
    }

    public void shutdown() {
        CompletableFuture<Void> future = this.getShutdownCacheFutures();
        this.makeProgressBar();
        future.join();
    }

    private void makeProgressBar() {
        int maxProgress = this.getMaxProgress();
        if (maxProgress > 0) {
            ProgressBar progressBar = new ProgressBarBuilder()
                    .setTaskName("Saving caches...")
                    .setInitialMax(maxProgress)
                    .setUpdateIntervalMillis(50)
                    .build();
            this.executor.scheduleAtFixedRate(() -> progressBar.stepTo(this.getCurrentProgress()), 0, 50, TimeUnit.MILLISECONDS);
        }
    }

    private int getCurrentProgress() {
        int userCount = this.serverCache.getMap().values().stream().map(Server::getUserCache).map(userCache -> userCache.getShutdownData().getSaved().get()).mapToInt(Integer::intValue).sum();
        return userCount
                + this.serverCache.getShutdownData().getSaved().get()
                + this.giveawayCache.getShutdownData().getSaved().get()
                + this.finishedGiveawayCache.getShutdownData().getSaved().get()
                + this.scheduledGiveawayCache.getShutdownData().getSaved().get();
    }

    private int getMaxProgress() {
        int userCount = this.serverCache.getMap().values().stream().map(Server::getUserCache).map(userCache -> userCache.getShutdownData().getSize()).mapToInt(Integer::intValue).sum();
        return userCount
                + this.serverCache.getShutdownData().getSize()
                + this.giveawayCache.getShutdownData().getSize()
                + this.finishedGiveawayCache.getShutdownData().getSize()
                + this.scheduledGiveawayCache.getShutdownData().getSize();
    }

    private CompletableFuture<Void> getShutdownCacheFutures() {
        ExecutorService shutdownExecutor = Executors.newFixedThreadPool(400);
        this.serverCache.getStorage().setExecutorService(shutdownExecutor);
        this.giveawayCache.getStorage().setExecutorService(shutdownExecutor);
        this.finishedGiveawayCache.getStorage().setExecutorService(shutdownExecutor);
        this.scheduledGiveawayCache.getStorage().setExecutorService(shutdownExecutor);

        Set<CompletableFuture<Void>> futuresSet = Sets.newHashSet();
        this.serverCache.getMap().values().stream().map(Server::getUserCache).map(UserCache::shutdown).forEach(futuresSet::addAll);
        futuresSet.addAll(this.serverCache.shutdown());
        futuresSet.addAll(this.giveawayCache.shutdown());
        futuresSet.addAll(this.finishedGiveawayCache.shutdown());
        futuresSet.addAll(this.scheduledGiveawayCache.shutdown());
        CompletableFuture[] futures = futuresSet.toArray(new CompletableFuture[]{});
        return CompletableFuture.allOf(futures);
    }
}
