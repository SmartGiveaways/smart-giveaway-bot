package pink.zak.giveawaybot.commands.console.premium.subs;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.console.command.ConsoleSubCommand;
import pink.zak.giveawaybot.service.time.Time;

import java.util.List;

public class PremiumAddSub extends ConsoleSubCommand {

    public PremiumAddSub(GiveawayBot bot) {
        super(bot, false);

        this.addFlat("add");
        this.addArguments(Long.class, String.class); // server id, time
    }

    @Override
    public void onExecute(List<String> args) {
        Server server = this.parseServerInput(args, 1);
        if (server == null) {
            return;
        }
        String timeInput = this.parseArgument(args, 2);
        long milliseconds = Time.parse(this.parseArgument(args, 2));
        if (milliseconds == -1) {
            GiveawayBot.logger().error("Could not parse time input ({})", timeInput);
            return;
        }
        long premiumExpiry = server.premiumExpiry();
        premiumExpiry = (premiumExpiry == -1 ? System.currentTimeMillis() : premiumExpiry) + milliseconds;
        server.setPremiumExpiry(premiumExpiry);
        GiveawayBot.logger().info("{} {}ms to server's expiry", milliseconds > 0 ? "Adding" : "Removing", milliseconds);
        GiveawayBot.logger().info("The server {}'s premium will now expire in {}", server.id(), Time.format(server.timeToPremiumExpiry()));
    }
}
