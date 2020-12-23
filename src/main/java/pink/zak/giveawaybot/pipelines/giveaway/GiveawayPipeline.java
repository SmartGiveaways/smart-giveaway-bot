package pink.zak.giveawaybot.pipelines.giveaway;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Message;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.metrics.helpers.LatencyMonitor;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.pipelines.giveaway.steps.EntryCounterStep;
import pink.zak.giveawaybot.threads.ThreadFunction;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GiveawayPipeline {
    private final GiveawayController controller;
    private final ExecutorService executor;
    private final LatencyMonitor latencyMonitor;

    private final Set<CurrentGiveaway> delayedDeletions = Sets.newConcurrentHashSet();
    private final EntryCounterStep entryCounterStep;

    public GiveawayPipeline(GiveawayBot bot, GiveawayController controller) {
        this.controller = controller;
        this.executor = bot.getAsyncExecutor(ThreadFunction.GENERAL);
        this.latencyMonitor = bot.getLatencyMonitor();
        this.entryCounterStep = new EntryCounterStep(bot, controller);

        this.startCheckingDelays(bot.getThreadManager().getScheduler());
    }

    public void endGiveaway(CurrentGiveaway giveaway) {
        this.executor.submit(() -> {
            if (giveaway == null) {
                return;
            }
            if (!this.latencyMonitor.isLatencyUsable()) {
                this.delayedDeletions.add(giveaway);
                if (this.delayedDeletions.isEmpty()) {
                    GiveawayBot.getLogger().warn("Latency was not usable so didnt delete giveaway ({}ms)", this.latencyMonitor.getLastTiming());
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
            if (!this.delayedDeletions.isEmpty() && this.latencyMonitor.isLatencyUsable()) {
                GiveawayBot.getLogger().warn("Still could not end {} giveaways as latency is not usable {{}ms}", this.delayedDeletions.size(), this.latencyMonitor.getLastTiming());
                return;
            }
            for (CurrentGiveaway giveaway : this.delayedDeletions) {
                this.delayedDeletions.remove(giveaway);
                this.endGiveaway(giveaway);
            }
        }, 30, 30, TimeUnit.SECONDS);
    }
}
