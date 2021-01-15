package pink.zak.giveawaybot.discord.metrics.helpers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.cache.singular.CachedValue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GenericMetrics {
    private final long startTime = System.currentTimeMillis();
    private final AtomicInteger entryCount = new AtomicInteger();
    private CachedValue<Long> guilds;
    private CachedValue<Integer> users;

    public GenericMetrics(GiveawayBot bot) {
        this.addJdaStats(bot);
    }

    public AtomicInteger getEntryCount() {
        return this.entryCount;
    }

    public int resetEntryCount() {
        return this.entryCount.getAndUpdate(current -> 0);
    }

    public long getGuilds() {
        return this.guilds.get();
    }

    public int getUsers() {
        return this.users.get();
    }

    public long getUptime() {
        return System.currentTimeMillis() - this.startTime;
    }

    private void addJdaStats(GiveawayBot bot) {
        this.guilds = new CachedValue<>(TimeUnit.SECONDS, 5, () -> bot.getShardManager().getGuildCache().size());
        this.users = new CachedValue<>(TimeUnit.SECONDS, 30, () -> {
            int counter = 0;
            for (Guild guild : bot.getShardManager().getGuildCache().asSet()) {
                counter += guild.getMemberCount();
            }
            return counter;
        });
    }
}
