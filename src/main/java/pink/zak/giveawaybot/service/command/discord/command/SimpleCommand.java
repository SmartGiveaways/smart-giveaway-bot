package pink.zak.giveawaybot.service.command.discord.command;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import pink.zak.giveawaybot.GiveawayBot;

import java.util.Map;
import java.util.Set;

public abstract class SimpleCommand extends Command {
    private final String commandId;
    private final CommandData commandData;
    private Map<String, SubCommand> subCommands = Maps.newHashMap();

    protected SimpleCommand(GiveawayBot bot, String commandId, boolean manager, boolean premium) {
        super(bot, manager, premium);
        this.commandId = commandId;
        this.commandData = this.createCommandData();
    }

    public String getCommandId() {
        return this.commandId;
    }

    public Map<String, SubCommand> getSubCommands() {
        return this.subCommands;
    }

    public void setSubCommands(Set<SubCommand> subCommands) {
        Map<String, SubCommand> subCommandMap = Maps.newHashMap();
        for (SubCommand subCommand : subCommands)
            if (subCommand.getSubCommandGroupId() != null)
                subCommandMap.put(subCommand.getSubCommandGroupId() + "/" + subCommand.getSubCommandId(), subCommand);
            else
                subCommandMap.put(subCommand.getSubCommandId(), subCommand);
        this.subCommands = subCommandMap;
    }

    public void setSubCommands(SubCommand... subCommands) {
        this.setSubCommands(Sets.newHashSet(subCommands));
    }

    public CommandData getCommandData() {
        return this.commandData;
    }

    protected abstract CommandData createCommandData();
}