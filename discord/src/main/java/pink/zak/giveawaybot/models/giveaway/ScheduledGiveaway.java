package pink.zak.giveawaybot.models.giveaway;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pink.zak.giveawaybot.service.time.Time;

import java.util.UUID;

public class ScheduledGiveaway extends Giveaway {
    private final UUID uuid;

    public ScheduledGiveaway(UUID uuid, long channelId, long serverId, long startTime, long endTime,
                             int winnerAmount, String presetName, String giveawayItem) {
        super(channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem);
        this.uuid = uuid;
    }

    public ScheduledGiveaway(long channelId, long serverId, long startTime, long endTime,
                             int winnerAmount, String presetName, String giveawayItem) {
        this(UUID.randomUUID(), channelId, serverId, startTime,
                endTime, winnerAmount, presetName, giveawayItem);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public long getMillisToStart() {
        return this.startTime - System.currentTimeMillis();
    }

    @JsonIgnore
    public String getStartFormatted() {
        return Time.formatAsDateTime(this.startTime) + " UTC+0";
    }

    @Override
    public long getTimeToExpiry() {
        return this.endTime - System.currentTimeMillis();
    }

    @Override
    public boolean isActive() {
        return this.getMillisToStart() < 0 && this.getTimeToExpiry() > 0;
    }
}
