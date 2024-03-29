package pink.zak.giveawaybot.commands.discord.admin.subs.language;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.lang.model.Language;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.BotSubCommand;

import java.util.Map;

public class ListLanguagesSub extends BotSubCommand {
    private final Map<String, MessageEmbed> messageEmbeds = Maps.newHashMap();

    public ListLanguagesSub(GiveawayBot bot) {
        super(bot, "language", "list", false, false);

        this.buildMessages(bot.getDefaults().getPalette());
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        event.replyEmbeds(this.messageEmbeds.get(server.getLanguage()))
            .setEphemeral(true).queue();
    }

    private void buildMessages(Palette palette) {
        StringBuilder description = new StringBuilder();
        for (Language language : this.languageRegistry.languageMap().values()) {
            description.append(language.getFlag())
                .append(" ").append(language.getName())
                .append(" - **")
                .append(language.getCoverage()).append("% coverage**");
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
