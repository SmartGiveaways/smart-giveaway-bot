package pink.zak.giveawaybot.pipelines.giveaway.steps;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.cache.UserCache;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.storage.GiveawayStorage;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

public class DeletionStep {
    private final FinishedGiveawayCache finishedGiveawayCache;
    private final GiveawayCache giveawayCache;
    private final GiveawayStorage giveawayStorage;
    private final ServerCache serverCache;

    private final Map<CurrentGiveaway, ScheduledFuture<?>> scheduledFutures;

    public DeletionStep(GiveawayBot bot, GiveawayController giveawayController) {
        this.finishedGiveawayCache = bot.getFinishedGiveawayCache();
        this.giveawayCache = bot.getGiveawayCache();
        this.scheduledFutures = giveawayController.getScheduledFutures();
        this.giveawayStorage = bot.getGiveawayStorage();
        this.serverCache = bot.getServerCache();
    }

    public DeletionStep(GiveawayBot bot) {
        this(bot, bot.getGiveawayController());
    }


    public void delete(CurrentGiveaway giveaway) {
        this.giveawayCache.invalidate(giveaway.messageId(), false);
        this.serverCache.get(giveaway.serverId()).thenAccept(server -> {
            if (this.scheduledFutures.containsKey(giveaway) && !this.scheduledFutures.get(giveaway).isDone()) {
                this.scheduledFutures.get(giveaway).cancel(false);
                this.scheduledFutures.remove(giveaway);
            }
            server.activeGiveaways().remove(giveaway.messageId());
            GiveawayBot.logger().debug("Removing giveaway from server {}  :  {}", giveaway.serverId(), giveaway.messageId());
            UserCache userCache = server.userCache();
            for (long enteredId : giveaway.enteredUsers()) {
                userCache.get(enteredId).thenAccept(user -> user.entries().remove(giveaway.messageId()));
            }
        });
        this.giveawayStorage.delete(giveaway.messageId());
    }

    public void addToFinished(Server server, CurrentGiveaway giveaway, BigInteger totalEntries, Map<Long, BigInteger> userEntries, Set<Long> winners) {
        FinishedGiveaway finishedGiveaway = new FinishedGiveaway(giveaway, totalEntries, userEntries, winners);
        server.finishedGiveaways().add(giveaway.messageId());
        this.finishedGiveawayCache.set(giveaway.messageId(), finishedGiveaway);
        this.finishedGiveawayCache.getStorage().save(finishedGiveaway);
    }
}
