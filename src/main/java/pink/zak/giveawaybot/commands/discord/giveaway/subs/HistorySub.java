package pink.zak.giveawaybot.commands.discord.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.menus.GiveawayHistoryMenu;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

import java.util.List;

public class HistorySub extends SubCommand {

    public HistorySub(GiveawayBot bot) {
        super(bot, true,false,false);

        this.addFlat("history");
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        new GiveawayHistoryMenu(super.bot, server).sendInitialMessage(event.getChannel());
    }
}
