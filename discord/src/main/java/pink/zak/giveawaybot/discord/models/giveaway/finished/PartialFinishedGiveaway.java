package pink.zak.giveawaybot.discord.models.giveaway.finished;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;
import pink.zak.giveawaybot.discord.models.giveaway.Giveaway;
import pink.zak.giveawaybot.discord.models.giveaway.RichGiveaway;

public class PartialFinishedGiveaway extends RichGiveaway implements Comparable<PartialFinishedGiveaway> {

    public PartialFinishedGiveaway(long messageId, long channelId, long serverId, long startTime, long endTime,
                                   int winnerAmount, String presetName, String giveawayItem) {
        super(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem);
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

    @Override
    public int compareTo(@NotNull PartialFinishedGiveaway other) {
        return Long.compare(other.getEndTime(), super.endTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RichGiveaway giveaway) {
            return giveaway.getMessageId() == this.messageId;
        }
        return false;
    }
}
