package pink.zak.giveawaybot.api.model.giveaway;

import pink.zak.giveawaybot.discord.data.models.giveaway.ScheduledGiveaway;

public class ScheduledGiveawayCreation {
    private long channelId;
    private long serverId;
    private long startTime;
    private long endTime;
    private int winnerAmount;
    private String presetName;
    private String giveawayItem;

    public long getChannelId() {
        return this.channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public long getServerId() {
        return this.serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getWinnerAmount() {
        return this.winnerAmount;
    }

    public void setWinnerAmount(int winnerAmount) {
        this.winnerAmount = winnerAmount;
    }

    public String getPresetName() {
        return this.presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    public String getGiveawayItem() {
        return this.giveawayItem;
    }

    public void setGiveawayItem(String giveawayItem) {
        this.giveawayItem = giveawayItem;
    }

    public ScheduledGiveaway toScheduledGiveaway(long serverId) {
        return new ScheduledGiveaway(this.channelId, serverId, this.startTime, this.endTime, this.winnerAmount, this.presetName, this.giveawayItem);
    }
}
