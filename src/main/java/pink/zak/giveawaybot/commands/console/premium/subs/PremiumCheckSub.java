package pink.zak.giveawaybot.commands.console.premium.subs;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.command.console.command.ConsoleSubCommand;
import pink.zak.giveawaybot.service.time.Time;

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
        JdaBot.LOGGER.info("-- [ Premium Info For {} ] --", server.getId());
        if (server.isPremium()) {
            JdaBot.LOGGER.info("Has Premium: Yes");
            JdaBot.LOGGER.info("Expires In: {}", Time.format(server.getTimeToPremiumExpiry()));
        } else {
            JdaBot.LOGGER.info("Has Premium: No");
            JdaBot.LOGGER.info("Expired: {}", server.getPremiumExpiry() == -1 ? "has never had premium" : Time.format(-server.getTimeToPremiumExpiry()) + " ago");
        }
        JdaBot.LOGGER.info("---------------------------------------------");
    }
}
