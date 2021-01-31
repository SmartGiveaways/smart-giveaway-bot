package pink.zak.giveawaybot.discord.commands.discord.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.commands.menus.GiveawayHistoryMenu;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;

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
