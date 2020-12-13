package pink.zak.giveawaybot.cache;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.service.cache.caches.AccessExpiringCache;

import java.util.concurrent.TimeUnit;

public class FinishedGiveawayCache extends AccessExpiringCache<Long, FinishedGiveaway> {

    public FinishedGiveawayCache(GiveawayBot bot) {
        super(bot, bot.getFinishedGiveawayStorage(), TimeUnit.MINUTES, 10);
    }

    @Override
    public FinishedGiveaway set(Long key, FinishedGiveaway value) {
        super.set(key, value);
        this.storage.save(value);
        return value;
    }

    public void shutdown() {
        this.invalidateAll();
    }
}
