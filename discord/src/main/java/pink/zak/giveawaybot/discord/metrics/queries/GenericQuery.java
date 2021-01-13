package pink.zak.giveawaybot.discord.metrics.queries;

import com.influxdb.client.write.Point;
import pink.zak.giveawaybot.discord.metrics.helpers.GenericBotMetrics;
import pink.zak.giveawaybot.discord.service.BotConstants;
import pink.zak.metrics.queries.QueryInterface;

import java.util.function.BiFunction;

public enum GenericQuery implements QueryInterface<GenericBotMetrics> {

    GUILDS((metrics, point) -> point.addField("guilds", metrics.getGuilds())),
    ENTRIES((metrics, point) -> point.addField("entries", metrics.resetEntryCount())),
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
        return (giveawayCache, point) -> point.addTag("system", BotConstants.getDeviceName());
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
