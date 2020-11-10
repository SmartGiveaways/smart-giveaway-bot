package pink.zak.giveawaybot.metrics.queries;

import com.influxdb.client.write.Point;
import pink.zak.giveawaybot.service.command.CommandBase;
import pink.zak.metrics.queries.QueryInterface;

import java.util.function.BiFunction;

public enum CommandQuery implements QueryInterface<CommandBase> {

    COMMAND_EXECUTIONS((commandBase, point) -> point.addField("command-executions", commandBase.retrieveExecutions()));

    private final BiFunction<CommandBase, Point, Point> computation;

    CommandQuery(BiFunction<CommandBase, Point, Point> computation) {
        this.computation = computation;
    }

    @Override
    public BiFunction<CommandBase, Point, Point> tag() {
        return (giveawayCache, point) -> point;
    }

    @Override
    public BiFunction<CommandBase, Point, Point> get() {
        return this.computation;
    }

    @Override
    public String measurement() {
        return "bot-metrics";
    }
}
