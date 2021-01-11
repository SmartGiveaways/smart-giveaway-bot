package pink.zak.giveawaybot.api.controllers;

import org.springframework.stereotype.Component;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.api.model.giveaway.ScheduledGiveawayCreation;
import pink.zak.giveawaybot.discord.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.discord.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.discord.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.models.giveaway.finished.FullFinishedGiveaway;
import pink.zak.giveawaybot.discord.models.giveaway.ScheduledGiveaway;

import java.util.UUID;

@Component
public class GiveawayControllerImpl implements GiveawayController {
    private final ScheduledGiveawayCache scheduledCache = GiveawayBot.apiInstance.getScheduledGiveawayCache();
    private final FinishedGiveawayCache finishedCache = GiveawayBot.apiInstance.getFinishedGiveawayCache();
    private final GiveawayCache currentCache = GiveawayBot.apiInstance.getGiveawayCache();

    @Override
    public ScheduledGiveaway getScheduledGiveaway(UUID uuid) {
        return this.scheduledCache.get(uuid);
    }

    @Override
    public ScheduledGiveaway createScheduledGiveaway(ScheduledGiveawayCreation payload) {
        return null;
    }

    @Override
    public CurrentGiveaway getCurrentGiveaway(long id) {
        return this.currentCache.get(id);
    }

    @Override
    public FullFinishedGiveaway getFinishedGiveaway(long id) {
        return this.finishedCache.get(id);
    }
}
