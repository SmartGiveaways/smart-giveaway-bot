package pink.zak.giveawaybot.metrics;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.models.Server;
import pink.zak.metrics.Metrics;
import pink.zak.metrics.queries.stock.SystemQuery;
import pink.zak.metrics.queries.stock.backends.ProcessStats;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MetricsStarter {

    public void start(GiveawayBot bot) {
        ScheduledExecutorService scheduledExecutor = bot.getThreadManager().getUpdaterExecutor();
        Metrics metrics = bot.getMetrics();

        ProcessStats processStats = new ProcessStats();
        GiveawayCache giveawayCache = bot.getGiveawayCache();
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
            for (Server server : serverCache.getMap().values()) {
                metrics.<Server>log(query -> query
                        .primary(server)
                        .push(ServerQuery.ALL)
                );
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
}
