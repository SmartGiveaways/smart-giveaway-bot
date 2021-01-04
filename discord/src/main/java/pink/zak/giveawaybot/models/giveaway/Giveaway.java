package pink.zak.giveawaybot.models.giveaway;

public abstract class   Giveaway {
    protected final long channelId;
    protected final long serverId;
    protected final long startTime;
    protected final long endTime;
    protected final int winnerAmount;
    protected final String presetName;
    protected final String giveawayItem;

    protected Giveaway(long channelId, long serverId, long startTime, long endTime,
                       int winnerAmount, String presetName, String giveawayItem) {
        this.channelId = channelId;
        this.serverId = serverId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.winnerAmount = winnerAmount;
        this.presetName = presetName;
        this.giveawayItem = giveawayItem;
    }

    public long getChannelId() {
        return this.channelId;
    }

    public long getServerId() {
        return this.serverId;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public int getWinnerAmount() {
        return this.winnerAmount;
    }

    public String getPresetName() {
        return this.presetName;
    }

    public String getGiveawayItem() {
        return this.giveawayItem;
    }

    public abstract long getTimeToExpiry();

    public abstract  boolean isActive();
}
