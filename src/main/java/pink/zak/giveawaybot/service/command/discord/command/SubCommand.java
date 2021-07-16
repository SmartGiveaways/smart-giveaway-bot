package pink.zak.giveawaybot.service.command.discord.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;

public abstract class SubCommand extends Command {
    private final String subCommandId;

    protected SubCommand(GiveawayBot bot, String subCommandId, boolean manager, boolean premium) {
        super(bot, manager, premium);
        this.subCommandId = subCommandId;
    }

    public String getSubCommandId() {
        return this.subCommandId;
    }

    @Override
    public abstract void onExecute(Member sender, Server server, SlashCommandEvent event);

    public abstract SubcommandData getSubCommandData();
}