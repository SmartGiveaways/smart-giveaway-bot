package pink.zak.giveawaybot.metrics.helpers;

import net.dv8tion.jda.api.JDA;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.cache.singular.CachedValue;

import java.util.concurrent.TimeUnit;

public class GenericBotMetrics {
    private CachedValue<Integer> guilds;

    public GenericBotMetrics(GiveawayBot bot) {
        this.addJdaStats(bot);
    }

    public int getGuilds() {
        return this.guilds.get();
    }

    private void addJdaStats(GiveawayBot bot) {
        this.guilds = new CachedValue<>(TimeUnit.MINUTES, 1, () -> {
            int guildCount = 0;
            for (JDA jda : bot.getShardManager().getShards()) {
                guildCount += jda.getGuilds().size();
            }
            return guildCount;
        });
    }
}
