package pink.zak.giveawaybot.api.cache;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.cache.caches.Cache;

public class TokenCache extends Cache<Void, Void> {

    public TokenCache(GiveawayBot bot) {
        super(bot);
    }
}
