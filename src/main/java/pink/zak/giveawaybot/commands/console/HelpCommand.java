package pink.zak.giveawaybot.commands.console;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.command.console.command.ConsoleBaseCommand;

import java.util.List;

public class HelpCommand extends ConsoleBaseCommand {
    
    public HelpCommand(GiveawayBot bot) {
        super(bot, "help");
    }

    @Override
    public void onExecute(List<String> args) {
        GiveawayBot.getLogger().info("help -> Prints this command.");
        GiveawayBot.getLogger().info("premium -> Premium commands.");
        GiveawayBot.getLogger().info("unload -> Data unloading commands.");
        GiveawayBot.getLogger().info("reload -> Reloads language values.");
        GiveawayBot.getLogger().info("stop -> Stops the bot and saves data.");
        GiveawayBot.getLogger().info("dump -> Creates a debug dump.");
    }
}
