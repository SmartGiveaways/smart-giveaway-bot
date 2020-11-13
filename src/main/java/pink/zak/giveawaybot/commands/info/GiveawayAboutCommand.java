package pink.zak.giveawaybot.commands.info;

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

import java.util.List;
import java.util.Map;

public class GiveawayAboutCommand extends SimpleCommand {
    private final Map<Language, MessageEmbed> messageEmbeds = Maps.newHashMap();
    private final

    public GiveawayAboutCommand(GiveawayBot bot) {
        super(bot, "gabout");
        this.setAliases("whatthisbotdo");

        this.buildMessages(bot.getLanguageRegistry(), bot.getDefaults().getPalette());
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        event.getTextChannel().sendMessage(this.messageEmbeds.get(server.getLanguage())).queue();
    }

    private void buildMessages(LanguageRegistry languageRegistry, Palette palette) {
        for (Language language : Language.values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(languageRegistry.get(language, Text.ABOUT_EMBED_TITLE).get())
                    .setFooter(languageRegistry.get(language, Text.GENERIC_EMBED_FOOTER).get())
                    .setColor(palette.primary())
                    .addField("General Commands", languageRegistry.get(language, Text.ABOUT_EMBED_CONTENT).get(), false);
            this.messageEmbeds.put(language, embedBuilder.build());
        }
    }
}
