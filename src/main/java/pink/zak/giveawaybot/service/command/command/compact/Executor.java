package pink.zak.giveawaybot.service.command.command.compact;

import net.dv8tion.jda.api.entities.Member;
import pink.zak.giveawaybot.service.command.command.SubCommand;

public interface Executor {

    void execute(Member sender, String[] args, SubCommand sub);
}
