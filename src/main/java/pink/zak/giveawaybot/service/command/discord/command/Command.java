package pink.zak.giveawaybot.service.command.discord.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.text.Replace;

import java.util.List;

public abstract class Command {
    protected final GiveawayBot bot;
    protected final LanguageRegistry languageRegistry;
    private final boolean manager;
    private final boolean requiresPremium;

    protected Command(GiveawayBot bot, boolean manager, boolean premium) {
        this.bot = bot;
        this.languageRegistry = bot.getLanguageRegistry();
        this.manager = manager;
        this.requiresPremium = premium;
    }

    public abstract void onExecute(Member sender, Server server, SlashCommandEvent event);

    public void middleMan(Member sender, Server server, SlashCommandEvent event) {
        this.onExecute(sender, server, event);
    }

    public boolean requiresManager() {
        return this.manager;
    }

    public boolean requiresPremium() {
        return this.requiresPremium;
    }

    protected LanguageRegistry.LangSub langFor(Server server, Text text, Replace replace) {
        return this.languageRegistry.get(server, text, replace);
    }

    protected LanguageRegistry.LangSub langFor(Server server, Text text) {
        return this.languageRegistry.get(server, text);
    }
}