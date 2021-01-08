package pink.zak.giveawaybot.discord.shutdown;

import com.google.common.collect.Sets;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.cache.*;
import pink.zak.giveawaybot.discord.models.Server;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ShutdownHelper {
    private final GiveawayBot bot;
    private final ServerCache serverCache;
    private final GiveawayCache giveawayCache;
    private final FinishedGiveawayCache finishedGiveawayCache;
    private final ScheduledGiveawayCache scheduledGiveawayCache;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public ShutdownHelper(GiveawayBot bot) {
        this.bot = bot;
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
        if (maxProgress > 5) {
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
