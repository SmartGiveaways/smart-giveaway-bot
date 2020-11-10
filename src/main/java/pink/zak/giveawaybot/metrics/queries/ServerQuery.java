package pink.zak.giveawaybot.metrics.queries;

import com.influxdb.client.write.Point;
import pink.zak.giveawaybot.models.Server;
import pink.zak.metrics.queries.QueryInterface;

import java.util.function.BiFunction;

public enum ServerQuery implements QueryInterface<Server> {

    GIVEAWAYS((server, point) -> {
        return point.addField("giveaway-count", server.getActiveGiveaways().size());
    }),
    PRESETS((server, point) -> {
        return point.addField("presets", server.getPresets().size());
    }),
    USER_CACHE_LOADS((server, point) -> {
        point.addField("user-cache-loads", server.getUserCache().getLoads());
        server.getUserCache().resetLoads();
        return point;
    }),
    USER_CACHE_HITS((server, point) -> {
        point.addField("user-cache-hits", server.getUserCache().getHits());
        server.getUserCache().resetHits();
        return point;
    }),
    ALL((server, point) -> {
        for (ServerQuery query : values()) {
            if (!query.toString().equals("ALL")) {
                query.get().apply(server, point);
            }
        }
        return point;
    });

    private final BiFunction<Server, Point, Point> computation;

    ServerQuery(BiFunction<Server, Point, Point> computation) {
        this.computation = computation;
    }

    @Override
    public BiFunction<Server, Point, Point> tag() {
        return (server, point) -> point.addTag("server-id", server.getStringId());
        // return (process, point) -> point.addTag("identifier", process.getIdentifier()); This should be made to the shard ID when that's a thing
    }

    @Override
    public BiFunction<Server, Point, Point> get() {
        return this.computation;
    }

    @Override
    public String measurement() {
        return "server-metrics";
    }
}
