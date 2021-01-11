package pink.zak.giveawaybot.discord.pipelines.giveaway.steps;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Message;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.models.giveaway.finished.FullFinishedGiveaway;
import pink.zak.giveawaybot.discord.service.types.NumberUtils;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class WinnerStep {
    private final MessageStep messageStep;

    public WinnerStep(MessageStep messageStep) {
        this.messageStep = messageStep;
    }

    public void actOnWinners(Server server, CurrentGiveaway giveaway, Message message, List<Long> enteredUsers, BigInteger totalEntries, Map<Long, BigInteger> userEntries) {
        int winnerAmount = giveaway.getWinnerAmount();
        if (userEntries.size() <= winnerAmount) {
            this.messageStep.handleFinishedMessages(server, giveaway, message, userEntries.keySet(), totalEntries, userEntries, true);
            return;
        }
        Set<Long> winners = this.generateWinners(winnerAmount, enteredUsers, totalEntries, userEntries);
        this.messageStep.handleFinishedMessages(server, giveaway, message, winners, totalEntries, userEntries, true);
    }

    private Set<Long> generateWinners(int winnerAmount, List<Long> enteredUsers, BigInteger totalEntries, Map<Long, BigInteger> userEntries) {
        Set<Long> winners = Sets.newHashSet();
        BigInteger currentTotalEntries = totalEntries;
        for (int i = 1; i <= winnerAmount; i++) { // For each winner to be generated
            BigInteger decreasingRandom = NumberUtils.getRandomBigInteger(currentTotalEntries);
            for (long userId : enteredUsers) {
                BigInteger entries = userEntries.get(userId);
                decreasingRandom = decreasingRandom.subtract(entries);
                if (decreasingRandom.compareTo(BigInteger.ONE) < 0) {
                    winners.add(userId);
                    if (i + 1 <= winnerAmount) {
                        enteredUsers.remove(userId);
                        currentTotalEntries = currentTotalEntries.subtract(entries);
                    }
                    break;
                }
            }
        }
        return winners;
    }

    public Set<Long> regenerateWinners(FullFinishedGiveaway giveaway) {
        List<Long> enteredUsers = Lists.newArrayList();
        enteredUsers.addAll(giveaway.getUserEntries().keySet());
        Collections.shuffle(enteredUsers, ThreadLocalRandom.current());
        return this.generateWinners(giveaway.getWinnerAmount(), enteredUsers, giveaway.getTotalEntries(), giveaway.getUserEntries());
    }
}
