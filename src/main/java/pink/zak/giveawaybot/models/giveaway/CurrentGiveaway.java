package pink.zak.giveawaybot.models.giveaway;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Message;

import java.util.Set;

public record CurrentGiveaway(long messageId, long channelId, long serverId, long startTime, long endTime,
                              int winnerAmount, String presetName, String giveawayItem,
                              Set<Long> enteredUsers) implements RichGiveaway {

    public CurrentGiveaway(long messageId, long channelId, long serverId, long startTime, long endTime, int winnerAmount, String presetName, String giveawayItem) {
        this(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem, Sets.newHashSet());
    }

    public CurrentGiveaway(long messageId, long channelId, long serverId, long endTime, int winnerAmount, String presetName, String giveawayItem) {
        this(messageId, channelId, serverId, System.currentTimeMillis(), endTime, winnerAmount, presetName, giveawayItem);
    }

    public CurrentGiveaway(long endTime, Message message, int winnerAmount, String presetName, String giveawayItem) {
        this(message.getIdLong(), message.getChannel().getIdLong(), message.getGuild().getIdLong(), endTime, winnerAmount, presetName, giveawayItem);
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

    @Override
    public String linkedGiveawayItem() {
        return "[" + this.giveawayItem + "](" + this.messageLink() + ")";
    }
}
