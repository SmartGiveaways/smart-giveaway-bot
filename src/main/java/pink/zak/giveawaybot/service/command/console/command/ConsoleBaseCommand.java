package pink.zak.giveawaybot.service.command.console.command;

import com.google.common.collect.Sets;
import pink.zak.giveawaybot.GiveawayBot;

import java.util.Arrays;
import java.util.Set;

public abstract class ConsoleBaseCommand extends ConsoleCommand {
    private final String command;
    private Set<String> aliases = Sets.newHashSet();
    private Set<ConsoleSubCommand> subCommands = Sets.newLinkedHashSet();

    public ConsoleBaseCommand(GiveawayBot bot, String command) {
        super(bot);
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }

    public Set<ConsoleSubCommand> getSubCommands() {
        return this.subCommands;
    }

    public void setSubCommands(Set<ConsoleSubCommand> subCommands) {
        this.subCommands = subCommands;
    }

    protected void setSubCommands(ConsoleSubCommand... subCommands) {
        this.subCommands.addAll(Arrays.asList(subCommands));
    }

    public Set<String> getAliases() {
        return this.aliases;
    }

    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
    }

    public void setAliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
    }

    public boolean doesCommandMatch(String command) {
        return this.command.equalsIgnoreCase(command) || this.aliases.contains(command);
    }
}
