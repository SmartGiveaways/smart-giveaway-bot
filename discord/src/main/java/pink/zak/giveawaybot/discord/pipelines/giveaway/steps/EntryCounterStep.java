package pink.zak.giveawaybot.discord.pipelines.giveaway.steps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.dv8tion.jda.api.entities.Message;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.controllers.GiveawayController;
import pink.zak.giveawaybot.discord.data.cache.ServerCache;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.data.models.User;
import pink.zak.giveawaybot.discord.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.pipelines.entries.EntryType;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EntryCounterStep {
    private final ServerCache serverCache;

    private final MessageStep messageStep;
    private final WinnerStep winnerStep;
    private final DeletionStep deletionStep;

    public EntryCounterStep(GiveawayBot bot, GiveawayController controller) {
        this.serverCache = bot.getServerCache();
        this.messageStep = new MessageStep(bot, controller);
        this.winnerStep = new WinnerStep(this.messageStep);
        this.deletionStep = new DeletionStep(bot, controller);
    }

    public void countEntries(CurrentGiveaway giveaway, Message message) {
        Server server = this.serverCache.get(giveaway.getServerId());
        BigInteger totalEntries = BigInteger.ZERO;
        Map<Long, BigInteger> userEntriesMap = Maps.newHashMap();
        List<Long> enteredUsers = Lists.newArrayList(giveaway.getEnteredUsers());
        Collections.shuffle(enteredUsers);
        long giveawayId = giveaway.getMessageId();

        for (long enteredUserId : giveaway.getEnteredUsers()) {
            User user = server.getUserCache().get(enteredUserId);
            if (user == null) {
                giveaway.getEnteredUsers().remove(enteredUserId);
                continue;
            }
            if (user.isBanned() || user.isShadowBanned()) {
                giveaway.getEnteredUsers().remove(enteredUserId);
                user.getEntries().remove(giveawayId);
                continue;
            }
            Map<EntryType, AtomicInteger> entries = user.getEntries().get(giveawayId);
            if (entries != null) {
                BigInteger totalUserEntries = BigInteger.ZERO;
                for (AtomicInteger entryTypeAmount : entries.values()) {
                    totalEntries = totalEntries.add(BigInteger.valueOf(entryTypeAmount.get()));
                    totalUserEntries = totalUserEntries.add(BigInteger.valueOf(entryTypeAmount.get()));
                }
                userEntriesMap.put(user.getId(), totalUserEntries);
            }
        }
        if (totalEntries.equals(BigInteger.ZERO)) {
            this.messageStep.sendEmptyMessage(giveaway, server, message);
            this.deletionStep.delete(giveaway);
        } else {
            this.winnerStep.actOnWinners(server, giveaway, message, enteredUsers, totalEntries, userEntriesMap);
        }
    }
}
