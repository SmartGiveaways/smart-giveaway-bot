package pink.zak.giveawaybot.models.giveaway;

import java.util.UUID;

public record ScheduledGiveaway(UUID uuid, long channelId, long serverId, long startTime, long endTime,
                                int winnerAmount, String presetName, String giveawayItem) implements Giveaway {

    public ScheduledGiveaway(long channelId, long serverId, long timeUntil, long length,
                             int winnerAmount, String presetName, String giveawayItem) {
        this(UUID.randomUUID(), channelId, serverId, System.currentTimeMillis() + timeUntil,
                System.currentTimeMillis() + timeUntil + length, winnerAmount, presetName, giveawayItem);
    }

    public long millisToStart() {
        return this.startTime - System.currentTimeMillis();
    }

    @Override
    public long timeToExpiry() {
        return this.endTime - System.currentTimeMillis();
    }

    @Override
    public boolean isActive() {
        return this.millisToStart() < 0 && this.timeToExpiry() > 0;
    }
}
