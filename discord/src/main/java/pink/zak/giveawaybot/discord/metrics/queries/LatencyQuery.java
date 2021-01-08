package pink.zak.giveawaybot.discord.metrics.queries;

import com.influxdb.client.write.Point;
import pink.zak.giveawaybot.discord.metrics.helpers.LatencyMonitor;
import pink.zak.metrics.queries.QueryInterface;

import java.util.function.BiFunction;

public enum LatencyQuery implements QueryInterface<LatencyMonitor> {

    LATENCY((monitor, point) -> point.addField("latency", monitor.getLastTiming()));

    private final BiFunction<LatencyMonitor, Point, Point> computation;

    LatencyQuery(BiFunction<LatencyMonitor, Point, Point> computation) {
        this.computation = computation;
    }

    @Override
    public BiFunction<LatencyMonitor, Point, Point> tag() {
        return (giveawayCache, point) -> point;
    }

    @Override
    public BiFunction<LatencyMonitor, Point, Point> get() {
        return this.computation;
    }

    @Override
    public String measurement() {
        return "bot-metrics";
    }
}
