package pink.zak.giveawaybot.discord.data.cache;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.discord.service.cache.caches.AccessExpiringCache;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ScheduledGiveawayCache extends AccessExpiringCache<UUID, ScheduledGiveaway> {

    public ScheduledGiveawayCache(GiveawayBot bot) {
        super(bot, bot.getScheduledGiveawayStorage(), TimeUnit.MINUTES, 10);
    }

    public void addScheduledGiveaway(ScheduledGiveaway giveaway) {
        this.storage.save(giveaway);
        this.set(giveaway.getUuid(), giveaway);
    }
}
