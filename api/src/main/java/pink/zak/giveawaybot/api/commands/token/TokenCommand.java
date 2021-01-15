package pink.zak.giveawaybot.api.commands.token;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleCommand;

import java.util.List;

public class TokenCommand extends ConsoleCommand {

    protected TokenCommand(GiveawayBot bot) {
        super(bot);
    }

    @Override
    public void onExecute(List<String> args) {
        JdaBot.logger.info("token list");
        JdaBot.logger.info("token create");
        JdaBot.logger.info("token info <token>");
        JdaBot.logger.info("token delete <token>");
    }
}
