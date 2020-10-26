package pink.zak.giveawaybot.models;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Message;

import java.util.Set;

public record Giveaway(long messageId, long channelId, long serverId, long startTime, long endTime, int winnerAmount, String presetName, String giveawayItem, Set<Long> enteredUsers) {

    public Giveaway(long messageId, long channelId, long serverId, long startTime, long endTime, int winnerAmount, String presetName, String giveawayItem) {
        this(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem, Sets.newHashSet());
    }

    public Giveaway(long messageId, long channelId, long serverId, long endTime, int winnerAmount, String presetName, String giveawayItem) {
        this(messageId, channelId, serverId, System.currentTimeMillis(), endTime, winnerAmount, presetName, giveawayItem);
    }

    public Giveaway(long endTime, Message message, int winnerAmount, String presetName, String giveawayItem) {
        this(message.getIdLong(), message.getChannel().getIdLong(), message.getGuild().getIdLong(), endTime, winnerAmount, presetName, giveawayItem);
    }

    public long getTimeToExpiry() {
        return this.endTime - System.currentTimeMillis();
    }

    public boolean isActive() {
        return this.getTimeToExpiry() > 0;
    }
}
