package pink.zak.giveawaybot.discord.commands.console;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleBaseCommand;

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
            GiveawayBot.logger().error("The bot is not initialized yet.");
        }
    }
}
