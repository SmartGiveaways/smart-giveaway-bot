package pink.zak.giveawaybot.discord.service.command.discord.command;

import com.google.common.collect.Sets;
import pink.zak.giveawaybot.discord.GiveawayBot;

import java.util.Arrays;
import java.util.Set;

public abstract class SimpleCommand extends Command {
    private final String command;
    private Set<String> aliases = Sets.newHashSet();
    private Set<SubCommand> subCommands = Sets.newLinkedHashSet();

    protected SimpleCommand(GiveawayBot bot, String command, boolean manager, boolean premium) {
        super(bot, manager, premium);
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }

    public Set<SubCommand> getSubCommands() {
        return this.subCommands;
    }

    public void setSubCommands(Set<SubCommand> subCommands) {
        this.subCommands = subCommands;
    }

    protected void setSubCommands(SubCommand... subCommands) {
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