package pink.zak.giveawaybot.discord.service.command.console.command;

import pink.zak.giveawaybot.discord.GiveawayBot;

import java.util.List;

public abstract class ConsoleCommand {
    protected final GiveawayBot bot;

    protected ConsoleCommand(GiveawayBot bot) {
        this.bot = bot;
    }

    public abstract void onExecute(List<String> args);
}
