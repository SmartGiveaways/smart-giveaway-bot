package pink.zak.giveawaybot.pipelines.giveaway.steps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.dv8tion.jda.api.entities.Message;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.User;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;

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
        Server server = this.serverCache.get(giveaway.serverId()).join();
        BigInteger totalEntries = BigInteger.ZERO;
        Map<Long, BigInteger> userEntriesMap = Maps.newHashMap();
        List<Long> enteredUsers = Lists.newArrayList(giveaway.enteredUsers());
        Collections.shuffle(enteredUsers);
        long giveawayId = giveaway.messageId();

        for (long enteredUserId : giveaway.enteredUsers()) {
            User user = server.userCache().getSync(enteredUserId);
            if (user == null) {
                giveaway.enteredUsers().remove(enteredUserId);
                continue;
            }
            if (user.isBanned() || user.isShadowBanned()) {
                giveaway.enteredUsers().remove(enteredUserId);
                user.entries().remove(giveawayId);
                continue;
            }
            Map<EntryType, AtomicInteger> entries = user.entries().get(giveawayId);
            if (entries != null) {
                BigInteger totalUserEntries = BigInteger.ZERO;
                for (AtomicInteger entryTypeAmount : entries.values()) {
                    totalEntries = totalEntries.add(BigInteger.valueOf(entryTypeAmount.get()));
                    totalUserEntries = totalUserEntries.add(BigInteger.valueOf(entryTypeAmount.get()));
                }
                userEntriesMap.put(user.id(), totalUserEntries);
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
