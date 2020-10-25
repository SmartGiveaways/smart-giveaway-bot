package pink.zak.giveawaybot.metrics;

import com.influxdb.client.write.Point;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.metrics.queries.QueryInterface;

import java.util.function.BiFunction;

public enum GiveawayCacheQuery implements QueryInterface<GiveawayCache> {

    GIVEAWAYS((cache, point) -> {
        //GiveawayBot.getLogger().info("Adding cache siz " + cache.size());
        return point.addField("giveaway-count", cache.size());
    }),
    CACHE_HITS((cache, point) -> {
        //GiveawayBot.getLogger().info("Adding cache hits " + cache.getHits().get());
        point.addField("cache-hits", cache.getHits().get());
        cache.resetHits();
        return point;
    }),
    CACHE_LOADS((cache, point) -> {
        //GiveawayBot.getLogger().info("Adding cache loads " + cache.getLoads().get());
        point.addField("cache-loads", cache.getLoads().get());
        cache.resetLoads();
        return point;
    }),
    ALL((cache, point) -> {
        for (GiveawayCacheQuery query : values()) {
            if (!query.toString().equals("ALL")) {
                query.get().apply(cache, point);
            }
        }
        //System.out.println(" ");
        return point;
    });

    private final BiFunction<GiveawayCache, Point, Point> computation;

    GiveawayCacheQuery(BiFunction<GiveawayCache, Point, Point> computation) {
        this.computation = computation;
    }

    @Override
    public BiFunction<GiveawayCache, Point, Point> tag() {
        return (giveawayCache, point) -> point;
        // return (process, point) -> point.addTag("identifier", process.getIdentifier()); This should be made to the shard ID when that's a thing
    }

    @Override
    public BiFunction<GiveawayCache, Point, Point> get() {
        return this.computation;
    }

    @Override
    public String measurement() {
        return "giveaway-cache-metrics";
    }
}
