package pink.zak.giveawaybot.discord.data.cache;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.service.cache.caches.AccessExpiringCache;

import java.util.concurrent.TimeUnit;

public class GiveawayCache extends AccessExpiringCache<Long, CurrentGiveaway> {

    public GiveawayCache(GiveawayBot bot) {
        super(bot, bot.getGiveawayStorage(), TimeUnit.MINUTES, 10, TimeUnit.MINUTES, 2);
    }

    public void addGiveaway(CurrentGiveaway giveaway) {
        this.set(giveaway.getMessageId(), giveaway);
    }
}
