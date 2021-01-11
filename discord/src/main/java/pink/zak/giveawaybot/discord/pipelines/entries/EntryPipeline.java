package pink.zak.giveawaybot.discord.pipelines.entries;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.defaults.Defaults;
import pink.zak.giveawaybot.discord.enums.EntryType;
import pink.zak.giveawaybot.discord.models.Preset;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.models.User;
import pink.zak.giveawaybot.discord.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.pipelines.entries.steps.EligibilityCheckStep;
import pink.zak.giveawaybot.discord.threads.ThreadFunction;

import java.util.concurrent.ExecutorService;

public class EntryPipeline {
    private final EligibilityCheckStep checkStep;
    private final GiveawayCache giveawayCache;
    private final Preset defaultPreset;
    private final ExecutorService executor;

    public EntryPipeline(GiveawayBot bot) {
        this.checkStep = new EligibilityCheckStep(bot);
        this.giveawayCache = bot.getGiveawayCache();
        this.defaultPreset = Defaults.defaultPreset;
        this.executor = bot.getAsyncExecutor(ThreadFunction.GENERAL);
    }

    public void process(EntryType entryType, Server server, long userId) {
        this.executor.execute(() -> {
            if (server.getActiveGiveaways().isEmpty()) {
                return;
            }
            User user = server.getUserCache().get(userId);
            if (user.isBanned()) {
                return;
            }
            for (long giveawayId : server.getActiveGiveaways()) {
                CurrentGiveaway giveaway = this.giveawayCache.get(giveawayId);
                if (giveaway == null) {
                    return;
                }
                this.checkStep.process(entryType, user, giveaway, giveaway.getPresetName().equals("default") ? this.defaultPreset : server.getPreset(giveaway.getPresetName()));
            }
        });
    }
}
