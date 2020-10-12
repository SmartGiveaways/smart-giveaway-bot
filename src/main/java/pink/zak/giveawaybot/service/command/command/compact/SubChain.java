package pink.zak.giveawaybot.service.command.command.compact;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.command.argument.Argument;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

public class SubChain {
    private Set<SubCommand> subCommands = Sets.newHashSet();

    public Set<SubCommand> getSubCommands() {
        return this.subCommands;
    }

    public SubChain newSub(GiveawayBot bot, boolean requiresManager, UnaryOperator<ArgumentBuilder> builder, Executor executor) {
        List<Argument<?>> arguments = builder.apply(new ArgumentBuilder()).getArguments();
        SubCommand subCommand = new SubCommand(bot, requiresManager) {

            @Override
            public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
                executor.execute(sender, args, null);
            }
        };
        subCommand.setArguments(arguments);
        this.subCommands.add(subCommand);
        return this;
    }

    public SubChain newSub(GiveawayBot bot, UnaryOperator<ArgumentBuilder> builder, Executor executor) {
        return this.newSub(bot, false, builder, executor);
    }
}
