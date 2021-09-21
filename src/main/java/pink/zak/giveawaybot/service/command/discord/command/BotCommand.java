package pink.zak.giveawaybot.service.command.discord.command;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;

import java.util.Map;
import java.util.Set;

public abstract class BotCommand extends GenericBotCommand {
    private final String name;
    private final CommandData commandData;
    private Map<String, BotSubCommand> subCommands = Maps.newHashMap();

    private net.dv8tion.jda.api.interactions.commands.Command command;

    protected BotCommand(GiveawayBot bot, String name, boolean manager, boolean premium) {
        super(bot, manager, premium);
        this.name = name;
        this.commandData = this.createCommandData();
    }

    public String getName() {
        return this.name;
    }

    public Map<String, BotSubCommand> getSubCommands() {
        return this.subCommands;
    }

    public void setSubCommands(Set<BotSubCommand> subCommands) {
        Map<String, BotSubCommand> subCommandMap = Maps.newHashMap();
        for (BotSubCommand subCommand : subCommands)
            if (subCommand.getSubCommandGroupId() != null)
                subCommandMap.put(subCommand.getSubCommandGroupId() + "/" + subCommand.getSubCommandId(), subCommand);
            else
                subCommandMap.put(subCommand.getSubCommandId(), subCommand);
        this.subCommands = subCommandMap;
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {

    }

    public void setSubCommands(BotSubCommand... subCommands) {
        this.setSubCommands(Sets.newHashSet(subCommands));
    }

    public CommandData getCommandData() {
        return this.commandData;
    }

    public net.dv8tion.jda.api.interactions.commands.Command getCommand() {
        return this.command;
    }

    public void setCommand(net.dv8tion.jda.api.interactions.commands.Command command) {
        this.command = command;
    }

    protected abstract CommandData createCommandData();
}