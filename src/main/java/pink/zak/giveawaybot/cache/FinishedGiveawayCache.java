package pink.zak.giveawaybot.cache;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.service.cache.AccessExpiringCache;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FinishedGiveawayCache extends AccessExpiringCache<Long, FinishedGiveaway> {

    public FinishedGiveawayCache(GiveawayBot bot) {
        super(bot, bot.getFinishedGiveawayStorage(), TimeUnit.MINUTES, 10);
    }

    @Override
    public CompletableFuture<FinishedGiveaway> set(Long key, FinishedGiveaway value) {
        CompletableFuture<FinishedGiveaway> val = super.set(key, value);
        val.thenAccept(giveaway -> this.storage.save(String.valueOf(giveaway.messageId()), giveaway));
        return val;
    }

    public void shutdown() {
        this.invalidateAll();
    }
}
