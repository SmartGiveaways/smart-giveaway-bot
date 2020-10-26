package pink.zak.giveawaybot.models.giveaway;

public interface Giveaway {

    long messageId();

    long channelId();

    long serverId();

    long startTime();

    long endTime();

    int winnerAmount();

    String presetName();

    String giveawayItem();

    long timeToExpiry();

    boolean isActive();
}
