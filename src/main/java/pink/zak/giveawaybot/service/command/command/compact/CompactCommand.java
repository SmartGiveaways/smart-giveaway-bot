package pink.zak.giveawaybot.service.command.command.compact;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;

import java.util.function.UnaryOperator;

public abstract class CompactCommand extends SimpleCommand {

    public CompactCommand(GiveawayBot bot, String command, boolean requiresManager) {
        super(bot, requiresManager, command);
    }

    public CompactCommand(GiveawayBot bot, String command) {
        super(bot, command);
    }

    public void subChain(UnaryOperator<SubChain> operator) {
        SubChain subChain = operator.apply(new SubChain());
        this.setSubCommands(subChain.getSubCommands());
    }
}
