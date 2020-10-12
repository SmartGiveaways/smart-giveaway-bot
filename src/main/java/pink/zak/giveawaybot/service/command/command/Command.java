package pink.zak.giveawaybot.service.command.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;

import java.util.List;

public abstract class Command {
    protected final GiveawayBot bot;
    private final boolean requiresManager;

    public Command(GiveawayBot bot, boolean requiresManager) {
        this.bot = bot;
        this.requiresManager = requiresManager;
    }

    public abstract void onExecute(Member sender, MessageReceivedEvent event, List<String> args);

    public void middleMan(Member sender, MessageReceivedEvent event, List<String> args) {
        this.onExecute(sender, event, args);
    }

    public boolean requiresManager() {
        return this.requiresManager;
    }
}