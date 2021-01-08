package pink.zak.giveawaybot.discord.models.giveaway;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

public class FinishedGiveaway extends RichGiveaway implements Comparable<FinishedGiveaway> {
    private final BigInteger totalEntries;
    private final Map<Long, BigInteger> userEntries;
    private Set<Long> winners;

    public FinishedGiveaway(long messageId, long channelId, long serverId, long startTime, long endTime,
                            int winnerAmount, String presetName, String giveawayItem, BigInteger totalEntries,
                            Map<Long, BigInteger> userEntries, Set<Long> winners) {
        super(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem);
        this.totalEntries = totalEntries;
        this.userEntries = userEntries;
        this.winners = winners;
    }

    public FinishedGiveaway(CurrentGiveaway giveaway, BigInteger totalEntries, Map<Long, BigInteger> userEntries, Set<Long> winners) {
        this(giveaway.getMessageId(), giveaway.getChannelId(), giveaway.getServerId(), giveaway.getStartTime(),
                giveaway.getEndTime(), giveaway.getWinnerAmount(), giveaway.getPresetName(), giveaway.getGiveawayItem(),
                totalEntries, userEntries, winners);
    }

    @Override
    @JsonIgnore
    public long getTimeToExpiry() {
        return this.endTime - System.currentTimeMillis();
    }

    @JsonIgnore
    public boolean isActive() {
        return this.getTimeToExpiry() > 0;
    }

    public BigInteger getTotalEntries() {
        return this.totalEntries;
    }

    public Map<Long, BigInteger> getUserEntries() {
        return this.userEntries;
    }

    public Set<Long> getWinners() {
        return this.winners;
    }

    public void setWinners(Set<Long> winners) {
        this.winners = winners;
    }

    @Override
    public int compareTo(@NotNull FinishedGiveaway other) {
        return Long.compare(other.getEndTime(), this.endTime);
    }
}
