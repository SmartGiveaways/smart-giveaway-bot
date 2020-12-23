package pink.zak.giveawaybot.metrics.helpers;

import net.dv8tion.jda.api.JDA;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.cache.singular.CachedValue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GenericBotMetrics {
    private final long startTime = System.currentTimeMillis();
    private final AtomicInteger entryCount = new AtomicInteger();
    private CachedValue<Integer> guilds;

    public GenericBotMetrics(GiveawayBot bot) {
        this.addJdaStats(bot);
    }

    public int getGuilds() {
        return this.guilds.get();
    }

    public long getUptime() {
        return System.currentTimeMillis() - this.startTime;
    }

    public AtomicInteger getEntryCount() {
        return this.entryCount;
    }

    public int resetEntryCount() {
        return this.entryCount.getAndUpdate(current -> 0);
    }

    private void addJdaStats(GiveawayBot bot) {
        this.guilds = new CachedValue<>(TimeUnit.MINUTES, 5, () -> {
            int guildCount = 0;
            for (JDA jda : bot.getShardManager().getShards()) {
                guildCount += jda.getGuilds().size();
            }
            return guildCount;
        });
    }
}
