package pink.zak.giveawaybot.discord.pipelines.giveaway.steps;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.discord.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.cache.ServerCache;
import pink.zak.giveawaybot.discord.cache.UserCache;
import pink.zak.giveawaybot.discord.controllers.GiveawayController;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.models.giveaway.finished.FullFinishedGiveaway;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.storage.GiveawayStorage;
import pink.zak.giveawaybot.discord.storage.finishedgiveaway.FullFinishedGiveawayStorage;

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
        JdaBot.logger.debug("Removing giveaway from server {}  :  {}", giveaway.getServerId(), messageId);
        UserCache userCache = server.getUserCache();
        for (long enteredId : giveaway.getEnteredUsers()) {
            userCache.get(enteredId).getEntries().remove(messageId);
        }
        this.giveawayStorage.delete(messageId);
    }

    public void addToFinished(Server server, CurrentGiveaway giveaway, BigInteger totalEntries, Map<Long, BigInteger> userEntries, Set<Long> winners) {
        FullFinishedGiveaway finishedGiveaway = this.finishedGiveawayStorage.create(giveaway, totalEntries, userEntries, winners);
        server.getFinishedGiveaways().add(giveaway.getMessageId());
        this.finishedGiveawayCache.set(giveaway.getMessageId(), finishedGiveaway);
    }
}
