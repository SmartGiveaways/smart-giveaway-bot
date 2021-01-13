package pink.zak.giveawaybot.discord.metrics.helpers;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.http.HttpRequestEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.metrics.queries.LatencyQuery;
import pink.zak.metrics.Metrics;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LatencyMonitor extends ListenerAdapter {
    private final Map<JDA, Long> shardTimings = Maps.newConcurrentMap();
    private final Map<JDA, Long> lastShardTestTimes = Maps.newConcurrentMap();
    private final ShardManager shardManager;
    private final Metrics metrics;

    @SneakyThrows
    public LatencyMonitor(GiveawayBot bot) {
        this.metrics = bot.getMetrics();
        this.shardManager = bot.getShardManager();
        bot.getThreadManager().getScheduler().scheduleAtFixedRate(this::testLatency, 0, 30, TimeUnit.SECONDS);
    }

    private void testLatency() {
        for (JDA jda : this.shardTimings.keySet()) {
            if (this.metrics != null) {
                this.metrics.<LatencyMonitor, JDA>logAdvanced(query -> query
                        .primary(this)
                        .push(LatencyQuery.LATENCY, jda));
            }
        }
        this.shardManager.getShards().forEach(jda -> {
            if (this.lastShardTestTimes.containsKey(jda) && System.currentTimeMillis() - this.lastShardTestTimes.get(jda) > 7500) {
                return;
            }
            jda.getRestPing().queue(latency -> {
                this.shardTimings.put(jda, latency);
                this.lastShardTestTimes.put(jda, System.currentTimeMillis());
                if (latency >= 5000) {
                    GiveawayBot.logger().warn("Tested latency of shard {} was too high ({}ms)", jda.getShardInfo().getShardId(), latency);
                }
            }, ex -> this.onHttpException(ex, jda));
        });
    }

    private void testLatency(JDA jda) {
        jda.getRestPing().queue(latency -> {
            if (latency >= 5000) {
                GiveawayBot.logger().warn("Tested latency of shard {} was still too high ({}ms)", jda.getShardInfo().getShardId(), latency);
            } else {
                GiveawayBot.logger().info("Tested latency of shard {} is now usable ({}ms)", jda.getShardInfo().getShardId(), latency);
            }
            this.shardTimings.put(jda, latency);
            this.lastShardTestTimes.put(jda, System.currentTimeMillis());
        });
    }

    public void onHttpRequest(HttpRequestEvent event) {
        if (event.getResponse() != null) {
            JDA jda = event.getJDA();
            if (event.getResponse().getException() != null) {
                this.onHttpException(event.getResponse().getException(), jda);
            } else if (
                    this.shardTimings.containsKey(jda) && this.shardTimings.get(jda) >= 10000 && (
                            !this.lastShardTestTimes.containsKey(jda) ||
                                    System.currentTimeMillis() - this.lastShardTestTimes.get(jda) > 7500
                    )
            ) this.testLatency(jda);
        }
    }

    public void onException(ExceptionEvent event) {
        this.onHttpException(event.getCause(), event.getJDA());
    }

    public void onHttpException(Throwable ex, JDA jda) {
        if (ex instanceof NoRouteToHostException || ex instanceof UnknownHostException || ex instanceof ErrorResponseException) {
            GiveawayBot.logger().warn("Shard {} had a timeout exception ({})", jda.getShardInfo().getShardId(), ex.getClass().getSimpleName());
            this.shardTimings.put(jda, Long.MAX_VALUE);
        }
    }

    public boolean isLatencyDesirable(JDA jda) {
        return this.shardTimings.containsKey(jda) && this.shardTimings.get(jda) < 2000;
    }

    public boolean isLatencyUsable(JDA jda) {
        return this.shardTimings.containsKey(jda) && this.shardTimings.get(jda) < 5000;
    }

    public long getAverageLatency() {
        long total = this.shardTimings.values().stream().mapToLong(Long::longValue).sum();
        return total / this.shardTimings.size();
    }

    public long getLastTiming(JDA jda) {
        return this.shardTimings.getOrDefault(jda, Long.MAX_VALUE);
    }

    public Map<JDA, Long> getShardTimings() {
        return this.shardTimings;
    }
}
