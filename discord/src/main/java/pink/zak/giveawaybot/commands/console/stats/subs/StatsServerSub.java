package pink.zak.giveawaybot.commands.console.stats.subs;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.console.command.ConsoleSubCommand;
import pink.zak.giveawaybot.service.time.Time;

import java.util.List;

public class StatsServerSub extends ConsoleSubCommand {

    public StatsServerSub(GiveawayBot bot) {
        super(bot, false);

        this.addArgument(Long.class); // server id
    }

    @Override
    public void onExecute(List<String> args) {
        Server server = this.parseServerInput(args, 0);
        if (server == null) {
            return;
        }
        GiveawayBot.logger().info("-- [ Stats Info For {} ] --", server.id());
        GiveawayBot.logger().info("Premium: {}", server.isPremium() ? "expires in ".concat(Time.format(server.timeToPremiumExpiry())) :
                server.premiumExpiry() == -1 ? "has never had" : "expired " + Time.format(-server.timeToPremiumExpiry()) + " ago");
        GiveawayBot.logger().info("Loaded Users: {}", server.userCache().size());
        GiveawayBot.logger().info("Giveaway Count: {}", server.activeGiveaways().size());
        GiveawayBot.logger().info("Scheduled Giveaway Count: {}", server.scheduledGiveaways().size());
        GiveawayBot.logger().info("Preset Count: {}", server.presets().size());
        GiveawayBot.logger().info("Ban Count: {}", server.bannedUsers().size());
        GiveawayBot.logger().info("-------------------------------------------");
    }
}
