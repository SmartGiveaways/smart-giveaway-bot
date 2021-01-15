package pink.zak.giveawaybot.discord.metrics.queries;

import com.influxdb.client.write.Point;
import net.dv8tion.jda.api.JDA;
import pink.zak.giveawaybot.discord.metrics.helpers.LatencyMonitor;
import pink.zak.giveawaybot.discord.service.BotConstants;
import pink.zak.metrics.queries.AdvancedQueryInterface;
import pink.zak.metrics.service.TriFunction;

public enum LatencyQuery implements AdvancedQueryInterface<LatencyMonitor, JDA> {

    LATENCY(((monitor, shardJda, point) ->
            point.addField("latency", monitor.getShardTimings().get(shardJda))
    ));

    private final TriFunction<LatencyMonitor, JDA, Point, Point> computation;

    LatencyQuery(TriFunction<LatencyMonitor, JDA, Point, Point> computation) {
        this.computation = computation;
    }

    @Override
    public TriFunction<LatencyMonitor, JDA, Point, Point> tag() {
        return (monitor, shard, point) -> point
                .addTag("shard", String.valueOf(shard.getShardInfo().getShardId()))
                .addTag("system", BotConstants.DEVICE_NAME);
    }

    @Override
    public TriFunction<LatencyMonitor, JDA, Point, Point> get() {
        return this.computation;
    }

    @Override
    public String measurement() {
        return "bot-metrics";
    }
}
