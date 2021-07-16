package pink.zak.giveawaybot.commands.console;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.command.console.command.ConsoleBaseCommand;

import java.util.List;

public class StopCommand extends ConsoleBaseCommand {

    public StopCommand(GiveawayBot bot) {
        super(bot, "stop");
    }

    @Override
    public void onExecute(List<String> args) {
        if (this.bot.isInitialized()) {
            System.exit(0);
        } else {
            JdaBot.LOGGER.error("The bot is not initialized yet.");
        }
    }
}
