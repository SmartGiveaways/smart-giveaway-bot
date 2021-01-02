package pink.zak.giveawaybot.service.command.console.command;

import pink.zak.giveawaybot.GiveawayBot;

import java.util.List;

public abstract class ConsoleCommand {
    protected final GiveawayBot bot;

    protected ConsoleCommand(GiveawayBot bot) {
        this.bot = bot;
    }

    public abstract void onExecute(List<String> args);
}
