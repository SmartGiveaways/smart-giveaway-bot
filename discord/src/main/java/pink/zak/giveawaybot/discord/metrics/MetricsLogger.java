package pink.zak.giveawaybot.discord.metrics;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.data.cache.ServerCache;
import pink.zak.giveawaybot.discord.metrics.helpers.GenericMetrics;
import pink.zak.giveawaybot.discord.metrics.queries.CommandQuery;
import pink.zak.giveawaybot.discord.metrics.queries.GenericQuery;
import pink.zak.giveawaybot.discord.metrics.queries.GiveawayCacheQuery;
import pink.zak.giveawaybot.discord.metrics.queries.ServerCacheQuery;
import pink.zak.giveawaybot.discord.metrics.queries.ServerQuery;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.command.discord.DiscordCommandBase;
import pink.zak.metrics.Metrics;
import pink.zak.metrics.queries.stock.SystemQuery;
import pink.zak.metrics.queries.stock.backends.ProcessStats;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MetricsLogger {
    private final GenericMetrics genericMetrics;
    private final ShardManager shardManager;

    public MetricsLogger(GiveawayBot bot) {
        this.genericMetrics = new GenericMetrics(bot);
        this.shardManager = bot.getShardManager();
    }

    public void checkAndStart(GiveawayBot bot) {
        if (!bot.getConfig("settings").bool("enable-metrics")) {
            JdaBot.logger.info("Metrics has not been enabled as it is disabled via configuration.");
            return;
        }
        ScheduledExecutorService scheduler = bot.getThreadManager().getScheduler();
        Metrics metrics = bot.getMetrics();

        ProcessStats processStats = new ProcessStats();
        GiveawayCache giveawayCache = bot.getGiveawayCache();
        DiscordCommandBase commandBase = bot.getDiscordCommandBase();
        ServerCache serverCache = bot.getServerCache();

        scheduler.scheduleAtFixedRate(() -> {
            metrics.<ProcessStats>log(query -> query
                    .primary(processStats)
                    .push(SystemQuery.ALL));
            metrics.<GiveawayCache>log(query -> query
                    .primary(giveawayCache)
                    .push(GiveawayCacheQuery.ALL)
            );
            metrics.<ServerCache>log(query -> query
                    .primary(serverCache)
                    .push(ServerCacheQuery.ALL)
            );
            metrics.<DiscordCommandBase>log(query -> query
                    .primary(commandBase)
                    .push(CommandQuery.COMMAND_EXECUTIONS));
            metrics.<GenericMetrics>log(query -> query
                    .primary(this.genericMetrics)
                    .push(GenericQuery.ALL));
            for (Server server : serverCache.getMap().values()) {
                Guild guild = this.shardManager.getGuildById(server.getId());
                metrics.<Server>log(query -> query
                        .primary(server)
                        .push(ServerQuery.ALL, Map.of("shard", guild == null ? "UNKNOWN" : String.valueOf(guild.getJDA().getShardInfo().getShardId())))
                );
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public GenericMetrics getGenericMetrics() {
        return this.genericMetrics;
    }

    public long getGuildCount() {
        return this.genericMetrics.getGuilds();
    }
}
