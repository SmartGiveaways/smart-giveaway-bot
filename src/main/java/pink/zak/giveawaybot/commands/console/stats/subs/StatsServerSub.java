package pink.zak.giveawaybot.commands.console.stats.subs;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.command.console.command.ConsoleSubCommand;
import pink.zak.giveawaybot.service.time.Time;

import java.util.List;

public class StatsServerSub extends ConsoleSubCommand {

    public StatsServerSub(GiveawayBot bot) {
        super(bot, bot.getServerCache(), false);

        this.addArgument(Long.class); // server id
    }

    @Override
    public void onExecute(List<String> args) {
        Server server = this.parseServerInput(args, 0);
        if (server == null) {
            return;
        }
        StringBuilder expiresMessage = new StringBuilder("Premium: {} ");
        if (server.isPremium()) {
            expiresMessage.append("expires in ")
                    .append(Time.format(server.getTimeToPremiumExpiry()));
        } else if (server.getPremiumExpiry() == -1) {
            expiresMessage.append("has never had");
        } else {
            expiresMessage.append("expired ")
                    .append(Time.format(-server.getTimeToPremiumExpiry()))
                    .append(" ago.");
        }
        JdaBot.LOGGER.info("-- [ Stats Info For {} ] --", server.getId());
        JdaBot.LOGGER.info(expiresMessage.toString(), server.getId());
        JdaBot.LOGGER.info("Loaded Users: {}", server.getUserCache().size());
        JdaBot.LOGGER.info("Giveaway Count: {}", server.getActiveGiveaways().size());
        JdaBot.LOGGER.info("Scheduled Giveaway Count: {}", server.getScheduledGiveaways().size());
        JdaBot.LOGGER.info("Preset Count: {}", server.getPresets().size());
        JdaBot.LOGGER.info("Ban Count: {}", server.getBannedUsers().size());
        JdaBot.LOGGER.info("-------------------------------------------");
    }
}
