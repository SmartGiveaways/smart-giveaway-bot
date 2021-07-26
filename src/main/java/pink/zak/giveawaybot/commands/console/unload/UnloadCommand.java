package pink.zak.giveawaybot.commands.console.unload;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.console.unload.subs.UnloadServerSub;
import pink.zak.giveawaybot.commands.console.unload.subs.UnloadUserSub;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.command.console.command.ConsoleBaseCommand;

import java.util.List;

public class UnloadCommand extends ConsoleBaseCommand {

    public UnloadCommand(GiveawayBot bot) {
        super(bot, "unload");

        this.setSubCommands(
            new UnloadServerSub(bot),
            new UnloadUserSub(bot)
        );
    }

    @Override
    public void onExecute(List<String> args) {
        JdaBot.LOGGER.info("unload <server> <server-id>");
    }
}
