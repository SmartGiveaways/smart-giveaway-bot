package pink.zak.giveawaybot.discord.commands.console;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleBaseCommand;

import java.util.List;

public class HelpCommand extends ConsoleBaseCommand {
    
    public HelpCommand(GiveawayBot bot) {
        super(bot, "help");
    }

    @Override
    public void onExecute(List<String> args) {
        GiveawayBot.logger().info("help -> Prints this command.");
        GiveawayBot.logger().info("premium -> Premium commands.");
        GiveawayBot.logger().info("unload -> Data unloading commands.");
        GiveawayBot.logger().info("reload -> Reloads language values.");
        GiveawayBot.logger().info("stop -> Stops the bot and saves data.");
        GiveawayBot.logger().info("dump -> Creates a debug dump.");
    }
}
