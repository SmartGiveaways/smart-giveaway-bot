package pink.zak.giveawaybot.discord.service.command.console;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleBaseCommand;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleSubCommand;
import pink.zak.giveawaybot.discord.service.command.global.CommandBackend;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ConsoleCommandBase extends CommandBackend {
    private final Set<ConsoleBaseCommand> commands = Sets.newHashSet();

    public ConsoleCommandBase(GiveawayBot bot) {
        super(bot);
    }

    public void registerCommands(ConsoleBaseCommand... commands) {
        this.commands.addAll(Arrays.asList(commands));
    }

    public void onExecute(String rawMessage) {
        String commandName = rawMessage.split(" ")[0];
        for (ConsoleBaseCommand simpleCommand : this.commands) {
            if (!simpleCommand.doesCommandMatch(commandName)) {
                continue;
            }
            if (!rawMessage.contains(" ")) {
                simpleCommand.onExecute(Lists.newArrayList());
                return;
            }
            String message = rawMessage.split(commandName + " ")[1];
            List<String> args = Arrays.asList(message.split(" "));
            args.removeIf(String::isEmpty);

            ConsoleSubCommand subResult = null;
            for (ConsoleSubCommand subCommand : simpleCommand.getSubCommands()) {
                if ((args.size() > subCommand.getArgumentsSize() && subCommand.isEndless() && subCommand.isEndlessMatch(args)) || (subCommand.getArgumentsSize() == args.size() && subCommand.isMatch(args))) {
                    subResult = subCommand;
                    break;
                }
            }
            if (subResult == null) {
                simpleCommand.onExecute(args);
                return;
            }
            subResult.onExecute(args);
        }
    }

    public Set<ConsoleBaseCommand> getCommands() {
        return this.commands;
    }
}
