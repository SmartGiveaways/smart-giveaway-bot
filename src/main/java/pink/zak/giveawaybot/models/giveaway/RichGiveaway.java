package pink.zak.giveawaybot.models.giveaway;

public interface RichGiveaway extends Giveaway {

    long messageId();

    String messageLink();

    String linkedGiveawayItem();
}
