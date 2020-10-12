package pink.zak.giveawaybot.service.command.command.compact;

import net.dv8tion.jda.api.entities.Member;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public interface Executor {

    void execute(Member sender, List<String> args, SubCommand sub);
}
