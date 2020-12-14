package pink.zak.giveawaybot.models.giveaway;

import pink.zak.giveawaybot.service.time.Time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public record ScheduledGiveaway(UUID uuid, long channelId, long serverId, long startTime, long endTime,
                                int winnerAmount, String presetName, String giveawayItem) implements Giveaway {

    public ScheduledGiveaway(long channelId, long serverId, long startTime, long endTime,
                             int winnerAmount, String presetName, String giveawayItem) {
        this(UUID.randomUUID(), channelId, serverId, startTime,
                endTime, winnerAmount, presetName, giveawayItem);
    }

    public long millisToStart() {
        return this.startTime - System.currentTimeMillis();
    }

    public String getStartFormatted() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.startTime), ZoneOffset.UTC).format(Time.getDateFormat()) + " UTC+0";
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
