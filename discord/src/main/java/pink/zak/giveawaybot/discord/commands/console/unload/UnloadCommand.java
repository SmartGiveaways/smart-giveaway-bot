package pink.zak.giveawaybot.discord.commands.console.unload;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.commands.console.unload.subs.UnloadServerSub;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.command.console.command.ConsoleBaseCommand;

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
        JdaBot.logger.info("unload <server> <server-id>");
    }
}
