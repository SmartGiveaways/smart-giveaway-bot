package pink.zak.giveawaybot.models.giveaway;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

public class FinishedGiveaway implements RichGiveaway, Comparable<FinishedGiveaway> {
    private final long messageId;
    private final long channelId;
    private final long serverId;
    private final long startTime;
    private final long endTime;
    private final int winnerAmount;
    private final String presetName;
    private final String giveawayItem;
    private final BigInteger totalEntries;
    private final Map<Long, BigInteger> userEntries;
    private Set<Long> winners;

    public FinishedGiveaway(long messageId, long channelId, long serverId, long startTime, long endTime,
                            int winnerAmount, String presetName, String giveawayItem, BigInteger totalEntries,
                            Map<Long, BigInteger> userEntries, Set<Long> winners) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.serverId = serverId;
        this.startTime=  startTime;
        this.endTime=endTime;
        this.winnerAmount = winnerAmount;
        this.presetName = presetName;
        this.giveawayItem = giveawayItem;
        this.totalEntries = totalEntries;
        this.userEntries = userEntries;
        this.winners = winners;
    }

    public FinishedGiveaway(CurrentGiveaway giveaway, BigInteger totalEntries, Map<Long, BigInteger> userEntries, Set<Long> winners) {
        this(giveaway.messageId(), giveaway.channelId(), giveaway.serverId(), giveaway.startTime(),
                giveaway.endTime(), giveaway.winnerAmount(), giveaway.presetName(), giveaway.giveawayItem(),
                totalEntries, userEntries, winners);
    }

    @Override
    public long channelId() {
        return this.channelId;
    }

    @Override
    public long serverId() {
        return this.serverId;
    }

    @Override
    public long startTime() {
        return this.startTime;
    }

    @Override
    public long endTime() {
        return this.endTime;
    }

    @Override
    public int winnerAmount() {
        return this.winnerAmount;
    }

    @Override
    public String presetName() {
        return this.presetName;
    }

    @Override
    public String giveawayItem() {
        return this.giveawayItem;
    }

    public long timeToExpiry() {
        return this.endTime - System.currentTimeMillis();
    }

    public boolean isActive() {
        return this.timeToExpiry() > 0;
    }

    @Override
    public long messageId() {
        return this.messageId;
    }

    @Override
    public String messageLink() {
        return "https://discord.com/channels/" + this.serverId + "/" + this.channelId + "/" + this.messageId;
    }

    @Override
    public String linkedGiveawayItem() {
        return "[" + this.giveawayItem + "](" + this.messageLink() + ")";
    }

    public BigInteger totalEntries() {
        return this.totalEntries;
    }

    public Map<Long, BigInteger> userEntries() {
        return this.userEntries;
    }

    public Set<Long> winners() {
        return this.winners;
    }

    public void setWinners(Set<Long> winners) {
        this.winners = winners;
    }

    @Override
    public int compareTo(@NotNull FinishedGiveaway other) {
        return Long.compare(other.endTime(), this.endTime);
    }
}
