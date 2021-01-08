package pink.zak.giveawaybot.discord.models.giveaway;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class RichGiveaway extends Giveaway {
    protected final long messageId;

    protected RichGiveaway(long messageId, long channelId, long serverId, long startTime, long endTime, int winnerAmount, String presetName, String giveawayItem) {
        super(channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem);
        this.messageId = messageId;
    }

    public long getMessageId() {
        return this.messageId;
    }

    public String getMessageLink() {
       return "https://discord.com/channels/" + this.serverId + "/" + this.channelId + "/" + this.messageId;
    }

    @JsonIgnore
    public String getLinkedGiveawayItem() {
        return "[" + this.giveawayItem + "](" + this.getMessageLink() + ")";
    }
}
