package pink.zak.giveawaybot.service.command.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.text.Replace;

import java.util.List;

public abstract class Command {
    protected final GiveawayBot bot;
    private final LanguageRegistry languageRegistry;
    private final boolean requiresManager;

    public Command(GiveawayBot bot, boolean requiresManager) {
        this.bot = bot;
        this.languageRegistry = bot.getLanguageRegistry();
        this.requiresManager = requiresManager;
    }

    public abstract void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args);

    public void middleMan(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        this.onExecute(sender, server, event, args);
    }

    public boolean requiresManager() {
        return this.requiresManager;
    }

    protected LanguageRegistry.LangSub langFor(Server server, Text text, Replace replace) {
        return this.languageRegistry.get(server, text, replace);
    }

    protected LanguageRegistry.LangSub langFor(Server server, Text text) {
        return this.languageRegistry.get(server, text);
    }
}