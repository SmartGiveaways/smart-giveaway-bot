package pink.zak.giveawaybot.service.command.command;

import com.google.common.collect.Sets;
import pink.zak.giveawaybot.GiveawayBot;

import java.util.Arrays;
import java.util.Set;

public abstract class SimpleCommand extends Command {
    private final String command;
    private Set<String> aliases = Sets.newHashSet();
    private Set<SubCommand> subCommands = Sets.newLinkedHashSet();

    public SimpleCommand(GiveawayBot bot, boolean allowBots, String command) {
        super(bot, allowBots);
        this.command = command;
    }

    public SimpleCommand(GiveawayBot bot, String command) {
        this(bot, true, command);
    }

    public String getCommand() {
        return this.command;
    }

    public Set<SubCommand> getSubCommands() {
        return this.subCommands;
    }

    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
    }

    public void setAliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
    }

    public Set<String> getAliases() {
        return this.aliases;
    }

    public void setSubCommands(Set<SubCommand> subCommands) {
        this.subCommands = subCommands;
    }

    protected void setSubCommands(SubCommand... subCommands) {
        this.subCommands.addAll(Arrays.asList(subCommands));
    }
}