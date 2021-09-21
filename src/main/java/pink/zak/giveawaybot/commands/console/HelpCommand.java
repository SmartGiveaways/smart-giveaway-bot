package pink.zak.giveawaybot.commands.console;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.command.console.command.ConsoleCommand;

import java.util.List;

public class HelpCommand extends ConsoleCommand {
    
    public HelpCommand(GiveawayBot bot) {
        super(bot, "help");
    }

    @Override
    public void onExecute(List<String> args) {
        JdaBot.LOGGER.info("help -> Prints this command.");
        JdaBot.LOGGER.info("premium -> Premium commands.");
        JdaBot.LOGGER.info("unload -> Data unloading commands.");
        JdaBot.LOGGER.info("reload -> Reloads language values.");
        JdaBot.LOGGER.info("stop -> Stops the bot and saves data.");
        JdaBot.LOGGER.info("dump -> Creates a debug dump.");
    }
}
