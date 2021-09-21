package pink.zak.giveawaybot.service.command.console.command;

import org.jetbrains.annotations.Nullable;
import pink.zak.giveawaybot.GiveawayBot;

import java.util.List;

public abstract class GenericConsoleCommand {

    @Nullable protected final GiveawayBot bot;

    protected GenericConsoleCommand(@Nullable GiveawayBot bot) {
        this.bot = bot;
    }

    public abstract void onExecute(List<String> args);
}
