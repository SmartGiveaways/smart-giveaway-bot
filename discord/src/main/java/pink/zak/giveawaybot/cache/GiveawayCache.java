package pink.zak.giveawaybot.cache;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.service.cache.caches.AccessExpiringCache;

import java.util.concurrent.TimeUnit;

public class GiveawayCache extends AccessExpiringCache<Long, CurrentGiveaway> {

    public GiveawayCache(GiveawayBot bot) {
        super(bot, bot.getGiveawayStorage(), TimeUnit.MINUTES, 10, TimeUnit.MINUTES, 2);
    }

    public void addGiveaway(CurrentGiveaway giveaway) {
        this.set(giveaway.getMessageId(), giveaway);
    }

    public void shutdown() {
        this.invalidateAll();
    }
}
