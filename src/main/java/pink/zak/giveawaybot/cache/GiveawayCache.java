package pink.zak.giveawaybot.cache;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.Giveaway;
import pink.zak.giveawaybot.service.cache.AccessExpiringCache;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GiveawayCache extends AccessExpiringCache<UUID, Giveaway> {

    public GiveawayCache(GiveawayBot bot) {
        super(bot, bot.getGiveawayStorage(), TimeUnit.MINUTES, 10);
    }

    public void addGiveaway(Giveaway giveaway) {
        this.set(giveaway.uuid(), giveaway);
    }

    public void shutdown() {
        this.invalidateAll();
    }
}
