package pink.zak.giveawaybot.discord.metrics.queries;

import com.influxdb.client.write.Point;
import pink.zak.giveawaybot.discord.metrics.helpers.GenericMetrics;
import pink.zak.giveawaybot.discord.service.BotConstants;
import pink.zak.metrics.queries.QueryInterface;

import java.util.function.BiFunction;

public enum GenericQuery implements QueryInterface<GenericMetrics> {

    GUILDS((metrics, point) -> point.addField("guilds", metrics.getGuilds())),
    USERS((metrics, point) -> point.addField("users", metrics.getUsers())),
    ENTRIES((metrics, point) -> point.addField("entries", metrics.resetEntryCount())),
    ALL((server, point) -> {
        for (GenericQuery query : values()) {
            if (!query.toString().equals("ALL")) {
                query.get().apply(server, point);
            }
        }
        return point;
    });

    private final BiFunction<GenericMetrics, Point, Point> computation;

    GenericQuery(BiFunction<GenericMetrics, Point, Point> computation) {
        this.computation = computation;
    }

    @Override
    public BiFunction<GenericMetrics, Point, Point> tag() {
        return (giveawayCache, point) -> point.addTag("system", BotConstants.DEVICE_NAME);
    }

    @Override
    public BiFunction<GenericMetrics, Point, Point> get() {
        return this.computation;
    }

    @Override
    public String measurement() {
        return "bot-metrics";
    }
}
