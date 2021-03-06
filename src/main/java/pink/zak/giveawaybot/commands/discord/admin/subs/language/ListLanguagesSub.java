package pink.zak.giveawaybot.commands.discord.admin.subs.language;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.lang.model.Language;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

import java.util.List;
import java.util.Map;

public class ListLanguagesSub extends SubCommand {
    private final Map<String, MessageEmbed> messageEmbeds = Maps.newHashMap();

    public ListLanguagesSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.addFlatWithAliases("languages", "language", "lang", "langs");

        this.buildMessages(bot.getDefaults().getPalette());
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(this.messageEmbeds.get(server.getLanguage())).queue();
    }

    private void buildMessages(Palette palette) {
        StringBuilder description = new StringBuilder();
        for (Language language : this.languageRegistry.languageMap().values()) {
            description.append(language.getFlag())
                    .append(" ").append(language.getName())
                    .append(" `(").append(language.getIdentifier()).append(")` - **")
                    .append(language.getCoverage()).append("%**.");
        }
        for (Language language : this.languageRegistry.languageMap().values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(language.getValue(Text.ADMIN_LIST_LANGUAGES_EMBED_TITLE).toString())
                    .setFooter(language.getValue(Text.ADMIN_LIST_LANGUAGES_EMBED_FOOTER).toString())
                    .setColor(palette.primary());
            embedBuilder.setDescription(description.toString());
            this.messageEmbeds.put(language.getIdentifier(), embedBuilder.build());
        }
    }
}
