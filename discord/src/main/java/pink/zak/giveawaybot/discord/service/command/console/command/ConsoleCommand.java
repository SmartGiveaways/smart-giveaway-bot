package pink.zak.giveawaybot.discord.service.command.console.command;

import org.jetbrains.annotations.Nullable;
import pink.zak.giveawaybot.discord.GiveawayBot;

import java.util.List;

public abstract class ConsoleCommand {

    @Nullable protected final GiveawayBot bot;

    protected ConsoleCommand(@Nullable GiveawayBot bot) {
        this.bot = bot;
    }

    public abstract void onExecute(List<String> args);
}
