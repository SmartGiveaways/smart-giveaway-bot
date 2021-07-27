package pink.zak.giveawaybot.data.models.giveaway.finished;

import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

public class FullFinishedGiveaway extends PartialFinishedGiveaway {
    private final BigInteger totalEntries;
    private final Map<Long, Integer> userEntries;
    private Set<Long> winners;

    public FullFinishedGiveaway(long messageId, long channelId, long serverId, long startTime, long endTime,
                                int winnerAmount, String presetName, String giveawayItem, BigInteger totalEntries,
                                Map<Long, Integer> userEntries, Set<Long> winners) {
        super(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem);
        this.totalEntries = totalEntries;
        this.userEntries = userEntries;
        this.winners = winners;
    }

    public FullFinishedGiveaway(CurrentGiveaway giveaway, BigInteger totalEntries, Map<Long, Integer> userEntries, Set<Long> winners) {
        this(giveaway.getMessageId(), giveaway.getChannelId(), giveaway.getServerId(), giveaway.getStartTime(),
                giveaway.getEndTime(), giveaway.getWinnerAmount(), giveaway.getPresetName(), giveaway.getGiveawayItem(),
                totalEntries, userEntries, winners);
    }

    public BigInteger getTotalEntries() {
        return this.totalEntries;
    }

    public Map<Long, Integer> getUserEntries() {
        return this.userEntries;
    }

    public Set<Long> getWinners() {
        return this.winners;
    }

    public void setWinners(Set<Long> winners) {
        this.winners = winners;
    }
}
