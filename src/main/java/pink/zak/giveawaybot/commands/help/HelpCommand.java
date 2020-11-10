package pink.zak.giveawaybot.commands.help;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.enums.Language;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;
import pink.zak.giveawaybot.threads.ThreadManager;

import java.util.List;
import java.util.Map;

public class HelpCommand extends SimpleCommand {
    private final ThreadManager threadManager;
    private final Palette palette;
    private final LanguageRegistry languageRegistry;
    private final Map<Language, MessageEmbed> limitedMessageEmbed = Maps.newHashMap();
    private final Map<Language, MessageEmbed> fullMessageEmbed = Maps.newHashMap();

    public HelpCommand(GiveawayBot bot) {
        super(bot, "ghelp");

        this.threadManager = bot.getThreadManager();
        this.palette = bot.getDefaults().getPalette();
        this.languageRegistry = bot.getLanguageRegistry();
        this.buildMessages();
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        event.getTextChannel().sendMessage(server.canMemberManage(sender) ? this.fullMessageEmbed.get(server.getLanguage()) : this.limitedMessageEmbed.get(server.getLanguage())).queue();
    }

    private void buildMessages() {
        for (Language language : Language.values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(this.languageRegistry.get(language, Text.HELP_EMBED_TITLE).get())
                    .setFooter(this.languageRegistry.get(language, Text.HELP_EMBED_FOOTER).get())
                    .setColor(this.palette.primary())
                    .addField("General Commands", this.languageRegistry.get(language, Text.HELP_LIMITED_SECTION).get(), false);
            this.limitedMessageEmbed.put(language, embedBuilder.build());
            embedBuilder.addField("Admin Commands",
                    """
                            >giveaway                  
                            >preset            
                            >gban <user> - Visibly bans a user from giveaways.
                            >gsban <user> - Shadow bans a user from giveaways. Almost impossible to tell.
                            >gunban <user> - Removes a user's ban or shadow ban.
                            """, false);
            this.fullMessageEmbed.put(language, embedBuilder.build());
        }
    }
}
