package pink.zak.giveawaybot.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.service.cache.caches.AccessExpiringCache;
import pink.zak.giveawaybot.storage.FinishedGiveawayStorage;

import java.util.Collections;
import java.util.List;
import java.util.Set;
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

    public List<FinishedGiveaway> getAll(Server server, boolean order) {
        Set<Long> remainingIds = Sets.newHashSet();
        List<FinishedGiveaway> giveaways = Lists.newArrayList();

        for (long giveawayId : server.finishedGiveaways()) {
            if (this.contains(giveawayId)) {
                giveaways.add(this.getSync(giveawayId));
            } else {
                remainingIds.add(giveawayId);
            }
        }
        if (remainingIds.isEmpty()) {
            return giveaways;
        }
        giveaways.addAll(((FinishedGiveawayStorage) this.storage).loadAll(server, remainingIds));
        if (order) {
            Collections.sort(giveaways);
        }
        return giveaways;
    }

    public void shutdown() {
        this.invalidateAll();
    }
}
