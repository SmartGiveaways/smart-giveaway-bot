package pink.zak.giveawaybot.discord.commands.console;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleBaseCommand;

import java.util.List;

public class HelpCommand extends ConsoleBaseCommand {
    
    public HelpCommand(GiveawayBot bot) {
        super(bot, "help");
    }

    @Override
    public void onExecute(List<String> args) {
        JdaBot.logger.info("help -> Prints this command.");
        JdaBot.logger.info("premium -> Premium commands.");
        JdaBot.logger.info("unload -> Data unloading commands.");
        JdaBot.logger.info("reload -> Reloads language values.");
        JdaBot.logger.info("stop -> Stops the bot and saves data.");
        JdaBot.logger.info("dump -> Creates a debug dump.");
    }
}
