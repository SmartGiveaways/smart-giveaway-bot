package pink.zak.giveawaybot.pipelines.giveaway;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.metrics.helpers.LatencyMonitor;
import pink.zak.giveawaybot.pipelines.giveaway.steps.EntryCounterStep;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.threads.ThreadFunction;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GiveawayPipeline {
    private final GiveawayController controller;
    private final ExecutorService executor;
    private final LatencyMonitor latencyMonitor;
    private final ShardManager shardManager;

    private final Set<CurrentGiveaway> delayedDeletions = Sets.newConcurrentHashSet();
    private final EntryCounterStep entryCounterStep;

    public GiveawayPipeline(GiveawayBot bot, GiveawayController controller) {
        this.controller = controller;
        this.executor = bot.getAsyncExecutor(ThreadFunction.GENERAL);
        this.latencyMonitor = bot.getLatencyMonitor();
        this.entryCounterStep = new EntryCounterStep(bot, controller);
        this.shardManager = bot.getShardManager();

        this.startCheckingDelays(bot.getThreadManager().getScheduler());
    }

    public void endGiveaway(CurrentGiveaway giveaway) {
        this.executor.submit(() -> {
            if (giveaway == null) {
                return;
            }
            Guild guild = this.shardManager.getGuildById(giveaway.getServerId());
            if (guild == null) {
                JdaBot.LOGGER.error("A Guild should not be null. ID: {}", giveaway.getServerId());
                return;
            }
            JDA jda = guild.getJDA();
            if (!this.latencyMonitor.isLatencyUsable(jda)) {
                this.delayedDeletions.add(giveaway);
                if (this.delayedDeletions.isEmpty()) {
                    JdaBot.LOGGER.warn("Latency was not usable so didnt delete giveaway ({}ms)", this.latencyMonitor.getLastTiming(jda));
                }
                return;
            }
            Message message = this.controller.getGiveawayMessage(giveaway);
            if (message != null) {
                this.entryCounterStep.countEntries(giveaway, message);
            }
        });
    }

    private void startCheckingDelays(ScheduledExecutorService scheduledExecutor) {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (!this.delayedDeletions.isEmpty()) {
                Set<JDA> affectedShards = Sets.newHashSet();
                Set<JDA> recoveredShards = Sets.newHashSet();
                int affectedGiveaways = 0;
                int recoveredGiveaways = 0;
                for (CurrentGiveaway giveaway : this.delayedDeletions) {
                    Guild guild = this.shardManager.getGuildById(giveaway.getServerId());
                    if (guild == null) {
                        JdaBot.LOGGER.error("B Guild should not be null. ID: {}", giveaway.getServerId());
                        return;
                    }
                    JDA jda = guild.getJDA();
                    if (!this.latencyMonitor.isLatencyUsable(jda)) {
                        affectedShards.add(jda);
                        affectedGiveaways++;
                        continue;
                    }
                    recoveredGiveaways++;
                    recoveredShards.add(jda);
                    this.delayedDeletions.remove(giveaway);
                    this.endGiveaway(giveaway);
                }
                JdaBot.LOGGER.warn("{} giveaways across {} shards recovered. {} giveaways across {} shards still could not be updated!",
                        recoveredGiveaways, recoveredShards.size(), affectedGiveaways, affectedShards.size());
            }
        }, 30, 30, TimeUnit.SECONDS);
    }
}
