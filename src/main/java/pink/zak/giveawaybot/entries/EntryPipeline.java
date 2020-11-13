package pink.zak.giveawaybot.entries;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.entries.workers.EligibilityCheckStep;
import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.models.Preset;

public class EntryPipeline {
    private final EligibilityCheckStep checkStep;
    private final ServerCache serverCache;
    private final GiveawayCache giveawayCache;
    private final Preset defaultPreset;

    public EntryPipeline(GiveawayBot bot) {
        this.checkStep = new EligibilityCheckStep();
        this.serverCache = bot.getServerCache();
        this.giveawayCache = bot.getGiveawayCache();
        this.defaultPreset = bot.getDefaults().getDefaultPreset();
    }

    public void process(EntryType entryType, long guildId, long userId) {
        this.serverCache.get(guildId).thenAccept(server -> {
            if (server.getActiveGiveaways().isEmpty()) {
                return;
            }
            server.getUserCache().get(userId).thenAccept(user -> {
                if (user.isBanned()) {
                    return;
                }
                for (long giveawayId : server.getActiveGiveaways()) {
                    this.giveawayCache.get(giveawayId).thenAccept(giveaway -> {
                        if (giveaway == null) {
                            return;
                        }
                        this.checkStep.process(entryType, user, giveaway, giveaway.presetName().equals("default") ? this.defaultPreset : server.getPreset(giveaway.presetName()));
                    }).exceptionally(ex -> {
                        GiveawayBot.getLogger().error("Server " + guildId + " user " + userId + " giveaway id " + giveawayId , ex);
                        return null;
                    });
                }
            });
        });
    }
}
