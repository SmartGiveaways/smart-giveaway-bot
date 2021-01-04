package pink.zak.giveawaybot.api.model.giveaway;

public class GiveawayCreation {
    private long channelId;
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
}
