package pink.zak.giveawaybot.cache;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.Giveaway;
import pink.zak.giveawaybot.service.cache.AccessExpiringCache;

import java.util.concurrent.TimeUnit;

public class GiveawayCache extends AccessExpiringCache<Long, Giveaway> {

    public GiveawayCache(GiveawayBot bot) {
        super(bot, bot.getGiveawayStorage(), TimeUnit.MINUTES, 10, TimeUnit.MINUTES, 2);
    }

    public void addGiveaway(Giveaway giveaway) {
        this.set(giveaway.messageId(), giveaway);
    }

    public void shutdown() {
        this.invalidateAll();
    }
}
