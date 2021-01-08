package pink.zak.giveawaybot.discord.metrics.queries;

import com.influxdb.client.write.Point;
import pink.zak.giveawaybot.discord.cache.ServerCache;
import pink.zak.metrics.queries.QueryInterface;

import java.util.function.BiFunction;

public enum ServerCacheQuery implements QueryInterface<ServerCache> {

    LOADED_SERVERS((cache, point) -> {
        return point.addField("loaded-servers", cache.size());
    }),
    CACHE_HITS((cache, point) -> {
        point.addField("cache-hits", cache.getHits().get());
        cache.resetHits();
        return point;
    }),
    CACHE_LOADS((cache, point) -> {
        point.addField("cache-loads", cache.getLoads().get());
        cache.resetLoads();
        return point;
    }),
    ALL((cache, point) -> {
        for (ServerCacheQuery query : values()) {
            if (!query.toString().equals("ALL")) {
                query.get().apply(cache, point);
            }
        }
        return point;
    });

    private final BiFunction<ServerCache, Point, Point> computation;

    ServerCacheQuery(BiFunction<ServerCache, Point, Point> computation) {
        this.computation = computation;
    }

    @Override
    public BiFunction<ServerCache, Point, Point> tag() {
        return (serverCache, point) -> point;
        // return (process, point) -> point.addTag("identifier", process.getIdentifier()); This should be made to the shard ID when that's a thing
    }

    @Override
    public BiFunction<ServerCache, Point, Point> get() {
        return this.computation;
    }

    @Override
    public String measurement() {
        return "server-cache-metrics";
    }
}
