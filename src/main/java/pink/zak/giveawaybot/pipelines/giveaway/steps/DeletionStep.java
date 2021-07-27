package pink.zak.giveawaybot.pipelines.giveaway.steps;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.data.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.data.cache.GiveawayCache;
import pink.zak.giveawaybot.data.cache.ServerCache;
import pink.zak.giveawaybot.data.cache.UserCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.data.models.giveaway.finished.FullFinishedGiveaway;
import pink.zak.giveawaybot.data.storage.GiveawayStorage;
import pink.zak.giveawaybot.data.storage.finishedgiveaway.FullFinishedGiveawayStorage;
import pink.zak.giveawaybot.service.bot.JdaBot;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

public class DeletionStep {
    private final FinishedGiveawayCache finishedGiveawayCache;
    private final FullFinishedGiveawayStorage finishedGiveawayStorage;
    private final GiveawayCache giveawayCache;
    private final GiveawayStorage giveawayStorage;
    private final ServerCache serverCache;

    private final Map<CurrentGiveaway, ScheduledFuture<Void>> scheduledFutures;

    public DeletionStep(GiveawayBot bot, GiveawayController giveawayController) {
        this.finishedGiveawayCache = bot.getFinishedGiveawayCache();
        this.finishedGiveawayStorage = bot.getFinishedGiveawayStorage();
        this.giveawayCache = bot.getGiveawayCache();
        this.scheduledFutures = giveawayController.getScheduledFutures();
        this.giveawayStorage = bot.getGiveawayStorage();
        this.serverCache = bot.getServerCache();
    }

    public DeletionStep(GiveawayBot bot) {
        this(bot, bot.getGiveawayController());
    }

    public void delete(CurrentGiveaway giveaway) {
        long messageId = giveaway.getMessageId();
        this.giveawayCache.invalidate(messageId, false);
        Server server = this.serverCache.get(giveaway.getServerId());
        if (this.scheduledFutures.containsKey(giveaway) && !this.scheduledFutures.get(giveaway).isDone()) {
            this.scheduledFutures.get(giveaway).cancel(false);
            this.scheduledFutures.remove(giveaway);
        }
        server.getActiveGiveaways().remove(messageId);
        JdaBot.LOGGER.debug("Removing giveaway from server {}  :  {}", giveaway.getServerId(), messageId);
        UserCache userCache = server.getUserCache();
        for (long enteredId : giveaway.getEnteredUsers()) {
            userCache.get(enteredId).getEntries().remove(messageId);
        }
        this.giveawayStorage.delete(messageId);
    }

    public void addToFinished(Server server, CurrentGiveaway giveaway, BigInteger totalEntries, Map<Long, Integer> userEntries, Set<Long> winners) {
        FullFinishedGiveaway finishedGiveaway = this.finishedGiveawayStorage.create(giveaway, totalEntries, userEntries, winners);
        server.getFinishedGiveaways().add(giveaway.getMessageId());
        this.finishedGiveawayCache.set(giveaway.getMessageId(), finishedGiveaway);
    }
}
