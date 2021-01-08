package pink.zak.giveawaybot.api.commands.token;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleCommand;

import java.util.List;

public class TokenCommand extends ConsoleCommand {

    protected TokenCommand(GiveawayBot bot) {
        super(bot);
    }

    @Override
    public void onExecute(List<String> args) {
        GiveawayBot.logger().info("token list");
        GiveawayBot.logger().info("token create");
        GiveawayBot.logger().info("token info <token>");
        GiveawayBot.logger().info("token delete <token>");
    }
}
