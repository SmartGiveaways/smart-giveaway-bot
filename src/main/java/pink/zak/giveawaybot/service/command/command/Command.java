package pink.zak.giveawaybot.service.command.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;

public abstract class Command {
    protected final GiveawayBot bot;
    private final boolean allowsBots;

    public Command(GiveawayBot bot, boolean allowBots) {
        this.bot = bot;
        this.allowsBots = allowBots;
    }

    public abstract void onExecute(Member sender, MessageReceivedEvent event, String[] args);

    public void middleMan(Member sender, MessageReceivedEvent event, String[] args) {
        this.onExecute(sender, event, args);
    }

    public boolean allowsBots() {
        return this.allowsBots;
    }
}