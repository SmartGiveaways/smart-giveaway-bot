package pink.zak.giveawaybot.commands.console;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.command.console.command.ConsoleBaseCommand;

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
