package pink.zak.giveawaybot.commands.discord.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.menus.GiveawayHistoryMenu;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

public class HistorySub extends SubCommand {

    public HistorySub(GiveawayBot bot) {
        super(bot, "history", true, false);
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        new GiveawayHistoryMenu(super.bot, server).sendInitialMessage(event);
    }
}
