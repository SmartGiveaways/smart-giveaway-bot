package pink.zak.giveawaybot.models;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

public record FinishedGiveaway(long messageId, long serverId, long startTime, long endTime,
                               int winnerAmount, String presetName, String giveawayItem, BigInteger totalEntries,
                               Map<Long, BigInteger> userEntries, Set<Long> winners) {
}
