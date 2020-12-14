package pink.zak.giveawaybot.commands.admin.subs.language;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.enums.Language;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;
import java.util.Map;

public class ListLanguagesSub extends SubCommand {
    private final Map<Language, MessageEmbed> messageEmbeds = Maps.newHashMap();

    public ListLanguagesSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.addFlatWithAliases("languages", "language", "lang", "langs");

        this.buildMessages(bot.getLanguageRegistry(), bot.getDefaults().getPalette());
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(this.messageEmbeds.get(server.getLanguage())).queue();
    }

    private void buildMessages(LanguageRegistry languageRegistry, Palette palette) {
        for (Language language : Language.values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(languageRegistry.get(language, Text.ADMIN_LIST_LANGUAGES_EMBED_TITLE).get())
                    .setFooter(languageRegistry.get(language, Text.ADMIN_LIST_LANGUAGES_EMBED_FOOTER).get())
                    .setColor(palette.primary());
            for (Language innerLang : Language.values()) {
                embedBuilder.addField("", innerLang.getIdentifiers()[0], true);
            }
            this.messageEmbeds.put(language, embedBuilder.build());
        }
    }
}
