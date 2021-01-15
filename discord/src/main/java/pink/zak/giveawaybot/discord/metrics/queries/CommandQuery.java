package pink.zak.giveawaybot.discord.metrics.queries;

import com.influxdb.client.write.Point;
import pink.zak.giveawaybot.discord.service.BotConstants;
import pink.zak.giveawaybot.discord.service.command.discord.DiscordCommandBase;
import pink.zak.metrics.queries.QueryInterface;

import java.util.function.BiFunction;

public enum CommandQuery implements QueryInterface<DiscordCommandBase> {

    COMMAND_EXECUTIONS((commandBase, point) -> point.addField("command-executions", commandBase.retrieveExecutions()));

    private final BiFunction<DiscordCommandBase, Point, Point> computation;

    CommandQuery(BiFunction<DiscordCommandBase, Point, Point> computation) {
        this.computation = computation;
    }

    @Override
    public BiFunction<DiscordCommandBase, Point, Point> tag() {
        return (giveawayCache, point) -> point.addTag("system", BotConstants.DEVICE_NAME);
    }

    @Override
    public BiFunction<DiscordCommandBase, Point, Point> get() {
        return this.computation;
    }

    @Override
    public String measurement() {
        return "bot-metrics";
    }
}
