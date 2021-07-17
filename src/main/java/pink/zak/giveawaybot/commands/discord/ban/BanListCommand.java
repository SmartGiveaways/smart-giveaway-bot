package pink.zak.giveawaybot.commands.discord.ban;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.menus.BanListMenu;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;

// todo use buttons for pageable menus
public class BanListCommand extends SimpleCommand {

    public BanListCommand(GiveawayBot bot) {
        super(bot, "banlist", true, false);
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        new BanListMenu(super.bot, server).sendInitialMessage(event);
    }

    @Override
    protected CommandData createCommandData() {
        return new CommandData("banlist", "List banned and shadowbanned users");
    }
}
