package pink.zak.giveawaybot.discord.commands.console.premium.subs;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleSubCommand;
import pink.zak.giveawaybot.discord.service.time.Time;

import java.util.List;

public class PremiumCheckSub extends ConsoleSubCommand {

    public PremiumCheckSub(GiveawayBot bot) {
        super(bot, bot.getServerCache(), false);

        this.addFlatWithAliases("check", "status");
        this.addArgument(Long.class); // server id
    }

    @Override
    public void onExecute(List<String> args) {
        Server server = this.parseServerInput(args, 1);
        if (server == null) {
            return;
        }
        JdaBot.logger.info("-- [ Premium Info For {} ] --", server.getId());
        if (server.isPremium()) {
            JdaBot.logger.info("Has Premium: Yes");
            JdaBot.logger.info("Expires In: {}", Time.format(server.getTimeToPremiumExpiry()));
        } else {
            JdaBot.logger.info("Has Premium: No");
            JdaBot.logger.info("Expired: {}", server.getPremiumExpiry() == -1 ? "has never had premium" : Time.format(-server.getTimeToPremiumExpiry()) + " ago");
        }
        JdaBot.logger.info("---------------------------------------------");
    }
}
