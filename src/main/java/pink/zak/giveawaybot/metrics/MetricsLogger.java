package pink.zak.giveawaybot.metrics;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.metrics.helpers.GenericBotMetrics;
import pink.zak.giveawaybot.metrics.queries.*;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.CommandBase;
import pink.zak.metrics.Metrics;
import pink.zak.metrics.queries.stock.SystemQuery;
import pink.zak.metrics.queries.stock.backends.ProcessStats;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MetricsLogger {
    private final GenericBotMetrics genericBotMetrics;

    public MetricsLogger(GiveawayBot bot) {
        this.genericBotMetrics = new GenericBotMetrics(bot);
    }

    public void checkAndStart(GiveawayBot bot) {
        if (!bot.getConfig("settings").bool("enable-metrics")) {
            GiveawayBot.getLogger().info("Metrics has not been enabled as it is disabled via configuration.");
            return;
        }
        ScheduledExecutorService scheduledExecutor = bot.getThreadManager().getScheduler();
        Metrics metrics = bot.getMetrics();

        ProcessStats processStats = new ProcessStats();
        GiveawayCache giveawayCache = bot.getGiveawayCache();
        CommandBase commandBase = bot.getCommandBase();
        ServerCache serverCache = bot.getServerCache();

        scheduledExecutor.scheduleAtFixedRate(() -> {
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
            metrics.<CommandBase>log(query -> query
                    .primary(commandBase)
                    .push(CommandQuery.COMMAND_EXECUTIONS));
            metrics.<GenericBotMetrics>log(query -> query
                    .primary(this.genericBotMetrics)
                    .push(GenericQuery.ALL));
            for (Server server : serverCache.getMap().values()) {
                metrics.<Server>log(query -> query
                        .primary(server)
                        .push(ServerQuery.ALL)
                );
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public GenericBotMetrics getGenericBotMetrics() {
        return this.genericBotMetrics;
    }

    public int getGuildCount() {
        return this.genericBotMetrics.getGuilds();
    }
}
