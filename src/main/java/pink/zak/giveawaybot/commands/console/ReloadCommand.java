package pink.zak.giveawaybot.commands.console;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.command.console.command.ConsoleCommand;

import java.util.List;

public class ReloadCommand extends ConsoleCommand {

    public ReloadCommand(GiveawayBot bot) {
        super(bot, "reload");
    }

    @Override
    public void onExecute(List<String> args) {
        this.bot.reload();
    }
}
