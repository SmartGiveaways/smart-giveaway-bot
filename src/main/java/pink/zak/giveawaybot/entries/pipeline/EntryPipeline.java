package pink.zak.giveawaybot.entries.pipeline;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.entries.pipeline.workers.EligibilityCheckStep;
import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.models.Preset;

import java.util.UUID;

public class EntryPipeline {
    private final EligibilityCheckStep checkStep;
    private final ServerCache serverCache;
    private final GiveawayCache giveawayCache;

    public EntryPipeline(GiveawayBot bot) {
        this.checkStep = new EligibilityCheckStep();
        this.serverCache = bot.getServerCache();
        this.giveawayCache = bot.getGiveawayCache();
    }

    public void process(EntryType entryType, long guildId, long userId) {
        this.serverCache.get(guildId).thenAccept(server -> {
            if (server.getActiveGiveaways().isEmpty()) {
                return;
            }
            server.getUserCache().get(userId).thenAccept(user -> {
                for (UUID giveawayId : server.getActiveGiveaways().values()) {
                    this.giveawayCache.get(giveawayId).thenAccept(giveaway -> {
                        Preset preset = server.getPreset(giveaway.presetName());
                        this.checkStep.process(entryType, user, giveaway, preset);
                    });
                }
            });
        });
    }
}
