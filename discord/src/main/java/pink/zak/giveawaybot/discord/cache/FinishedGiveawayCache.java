package pink.zak.giveawaybot.discord.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.discord.service.cache.caches.AccessExpiringCache;
import pink.zak.giveawaybot.discord.storage.FinishedGiveawayStorage;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class FinishedGiveawayCache extends AccessExpiringCache<Long, FinishedGiveaway> {

    public FinishedGiveawayCache(GiveawayBot bot) {
        super(bot, bot.getFinishedGiveawayStorage(), TimeUnit.MINUTES, 10);
    }

    @Override
    public FinishedGiveaway set(Long key, FinishedGiveaway value) {
        return this.set(key, value, true);
    }

    public FinishedGiveaway set(Long key, FinishedGiveaway value, boolean save) {
        super.set(key, value);
        if (save) {
            this.storage.save(value);
        }
        return value;
    }

    public List<FinishedGiveaway> getAll(Server server) {
        Set<Long> remainingIds = Sets.newHashSet();
        List<FinishedGiveaway> giveaways = Lists.newArrayList();

        for (long giveawayId : server.getFinishedGiveaways()) {
            if (this.contains(giveawayId)) {
                giveaways.add(this.get(giveawayId));
            } else {
                remainingIds.add(giveawayId);
            }
        }
        if (remainingIds.isEmpty()) {
            return giveaways;
        }
        Set<FinishedGiveaway> loadedGiveaways = ((FinishedGiveawayStorage) this.storage).loadAll(server, remainingIds);
        for (FinishedGiveaway giveaway : loadedGiveaways) {
            this.set(giveaway.getMessageId(), giveaway, false);
        }
        giveaways.addAll(loadedGiveaways);
        return giveaways;
    }
}
