package pink.zak.giveawaybot.discord.commands.console.stats.subs;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleSubCommand;
import pink.zak.giveawaybot.discord.service.time.Time;

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
        JdaBot.logger.info("-- [ Stats Info For {} ] --", server.getId());
        JdaBot.logger.info(expiresMessage.toString(), server.getId());
        JdaBot.logger.info("Loaded Users: {}", server.getUserCache().size());
        JdaBot.logger.info("Giveaway Count: {}", server.getActiveGiveaways().size());
        JdaBot.logger.info("Scheduled Giveaway Count: {}", server.getScheduledGiveaways().size());
        JdaBot.logger.info("Preset Count: {}", server.getPresets().size());
        JdaBot.logger.info("Ban Count: {}", server.getBannedUsers().size());
        JdaBot.logger.info("-------------------------------------------");
    }
}
