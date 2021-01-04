package pink.zak.giveawaybot.api.controllers;

import org.springframework.stereotype.Component;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.api.model.giveaway.ScheduledGiveawayCreation;
import pink.zak.giveawaybot.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.models.giveaway.ScheduledGiveaway;

import java.util.UUID;

@Component
public class GiveawayControllerImpl implements GiveawayController {
    private final ScheduledGiveawayCache scheduledCache = GiveawayBot.apiInstance.getScheduledGiveawayCache();
    private final FinishedGiveawayCache finishedCache = GiveawayBot.apiInstance.getFinishedGiveawayCache();
    private final GiveawayCache currentCache = GiveawayBot.apiInstance.getGiveawayCache();

    @Override
    public ScheduledGiveaway getScheduledGiveaway(UUID uuid) {
        return this.scheduledCache.getSync(uuid);
    }

    @Override
    public ScheduledGiveaway createScheduledGiveaway(ScheduledGiveawayCreation payload) {
        return null;
    }

    @Override
    public CurrentGiveaway getCurrentGiveaway(long id) {
        return this.currentCache.getSync(id);
    }

    @Override
    public FinishedGiveaway getFinishedGiveaway(long id) {
        return this.finishedCache.getSync(id);
    }
}
