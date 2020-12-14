package pink.zak.giveawaybot.models.giveaway;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

public record FinishedGiveaway(long messageId, long channelId, long serverId, long startTime, long endTime,
                               int winnerAmount, String presetName, String giveawayItem, BigInteger totalEntries,
                               Map<Long, BigInteger> userEntries, Set<Long> winners) implements RichGiveaway {

    public FinishedGiveaway(CurrentGiveaway giveaway, BigInteger totalEntries, Map<Long, BigInteger> userEntries, Set<Long> winners) {
        this(giveaway.messageId(), giveaway.channelId(), giveaway.serverId(), giveaway.startTime(),
                giveaway.endTime(), giveaway.winnerAmount(), giveaway.presetName(), giveaway.giveawayItem(),
                totalEntries, userEntries, winners);
    }

    public long timeToExpiry() {
        return this.endTime - System.currentTimeMillis();
    }

    public boolean isActive() {
        return this.timeToExpiry() > 0;
    }

    @Override
    public String messageLink() {
        return "https://discord.com/channels/" + this.serverId + "/" + this.channelId + "/" + this.messageId;
    }
}
