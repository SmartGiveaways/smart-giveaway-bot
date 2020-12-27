package pink.zak.giveawaybot.commands.console.premium.subs;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.console.command.ConsoleSubCommand;
import pink.zak.giveawaybot.service.time.Time;

import java.util.List;

public class PremiumCheckSub extends ConsoleSubCommand {

    public PremiumCheckSub(GiveawayBot bot) {
        super(bot, false);

        this.addFlatWithAliases("check", "status");
        this.addArgument(Long.class); // server id
    }

    @Override
    public void onExecute(List<String> args) {
        Server server = this.parseServerInput(args, 1);
        if (server == null) {
            return;
        }
        GiveawayBot.getLogger().info("-- [ Premium Info For {} ] --", server.id());
        if (server.isPremium()) {
            GiveawayBot.getLogger().info("Has Premium: Yes");
            GiveawayBot.getLogger().info("Expires In: {}", Time.format(server.timeToPremiumExpiry()));
        } else {
            GiveawayBot.getLogger().info("Has Premium: No");
            GiveawayBot.getLogger().info("Expired: {}", server.premiumExpiry() == -1 ? "has never had premium" : -server.timeToPremiumExpiry() + " ago");
        }
        GiveawayBot.getLogger().info("---------------------------------------------");
    }
}
