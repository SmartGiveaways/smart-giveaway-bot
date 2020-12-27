package pink.zak.giveawaybot.commands.console.unload;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.console.unload.subs.UnloadServerSub;
import pink.zak.giveawaybot.service.command.console.command.ConsoleBaseCommand;

import java.util.List;

public class UnloadCommand extends ConsoleBaseCommand {

    public UnloadCommand(GiveawayBot bot) {
        super(bot, "unload");

        this.setSubCommands(
                new UnloadServerSub(bot)
        );
    }

    @Override
    public void onExecute(List<String> args) {
        GiveawayBot.logger().info("unload <server> <server-id>");
    }
}
