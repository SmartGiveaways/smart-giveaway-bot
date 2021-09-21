package pink.zak.giveawaybot.commands.console.premium;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.console.premium.subs.PremiumAddSub;
import pink.zak.giveawaybot.commands.console.premium.subs.PremiumCheckSub;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.command.console.command.ConsoleCommand;

import java.util.List;

public class PremiumCommand extends ConsoleCommand {

    public PremiumCommand(GiveawayBot bot) {
        super(bot, "premium");

        this.setSubCommands(
                new PremiumCheckSub(bot),
                new PremiumAddSub(bot)
        );
    }

    @Override
    public void onExecute(List<String> args) {
        JdaBot.LOGGER.info("premium check <server-id>");
        JdaBot.LOGGER.info("premium add <server-id> <time>");
    }
}
