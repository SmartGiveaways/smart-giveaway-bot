package pink.zak.giveawaybot.discord.commands.console.premium;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.commands.console.premium.subs.PremiumAddSub;
import pink.zak.giveawaybot.discord.commands.console.premium.subs.PremiumCheckSub;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleBaseCommand;

import java.util.List;

public class PremiumCommand extends ConsoleBaseCommand {

    public PremiumCommand(GiveawayBot bot) {
        super(bot, "premium");

        this.setSubCommands(
                new PremiumCheckSub(bot),
                new PremiumAddSub(bot)
        );
    }

    @Override
    public void onExecute(List<String> args) {
        JdaBot.logger.info("premium check <server-id>");
        JdaBot.logger.info("premium add <server-id> <time>");
    }
}
