package pink.zak.giveawaybot.models.giveaway;

import java.util.UUID;

public record ScheduledGiveaway(UUID uuid, long channelId, long serverId, long startTime, long endTime,
                                int winnerAmount, String presetName, String giveawayItem) {

    public ScheduledGiveaway(long channelId, long serverId, long timeUntil, long length,
                             int winnerAmount, String presetName, String giveawayItem) {
        this(UUID.randomUUID(), channelId, serverId, System.currentTimeMillis() + timeUntil,
                System.currentTimeMillis() + timeUntil + length, winnerAmount, presetName, giveawayItem);
    }

    public long millisToStart() {
        return this.startTime - System.currentTimeMillis();
    }
}
