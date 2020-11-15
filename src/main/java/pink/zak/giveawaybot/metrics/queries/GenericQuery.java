package pink.zak.giveawaybot.metrics.queries;

import com.influxdb.client.write.Point;
import pink.zak.giveawaybot.metrics.helpers.GenericBotMetrics;
import pink.zak.metrics.queries.QueryInterface;

import java.util.function.BiFunction;

public enum GenericQuery implements QueryInterface<GenericBotMetrics> {

    GUILDS((metrics, point) -> point.addField("guilds", metrics.getGuilds())),
    ENTRIES((metrics, point) -> {
        int count = metrics.getEntryCount().get();
        metrics.resetEntryCount();
        return point.addField("entries", count);
    }),
    ALL((server, point) -> {
        for (GenericQuery query : values()) {
            if (!query.toString().equals("ALL")) {
                query.get().apply(server, point);
            }
        }
        return point;
    });

    private final BiFunction<GenericBotMetrics, Point, Point> computation;

    GenericQuery(BiFunction<GenericBotMetrics, Point, Point> computation) {
        this.computation = computation;
    }

    @Override
    public BiFunction<GenericBotMetrics, Point, Point> tag() {
        return (giveawayCache, point) -> point;
    }

    @Override
    public BiFunction<GenericBotMetrics, Point, Point> get() {
        return this.computation;
    }

    @Override
    public String measurement() {
        return "bot-metrics";
    }
}
