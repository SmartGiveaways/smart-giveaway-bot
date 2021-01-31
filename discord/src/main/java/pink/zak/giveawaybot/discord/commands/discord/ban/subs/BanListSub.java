package pink.zak.giveawaybot.discord.commands.discord.ban.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.commands.menus.BanListMenu;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;

import java.util.List;

public class BanListSub extends SubCommand {

    public BanListSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.addFlat("list");
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        new BanListMenu(super.bot, server).sendInitialMessage(event.getChannel());
    }
}
