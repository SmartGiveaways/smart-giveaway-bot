package pink.zak.giveawaybot.discord.commands.console;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleBaseCommand;

import java.util.List;

public class ReloadCommand extends ConsoleBaseCommand {

    public ReloadCommand(GiveawayBot bot) {
        super(bot, "reload");
    }

    @Override
    public void onExecute(List<String> args) {
        this.bot.reload();
    }
}
