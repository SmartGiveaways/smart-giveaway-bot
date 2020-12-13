package pink.zak.giveawaybot.commands.help;

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
import pink.zak.giveawaybot.service.command.command.SimpleCommand;

import java.util.List;
import java.util.Map;

public class HelpCommand extends SimpleCommand {
    private final Map<Language, MessageEmbed> limitedMessageEmbed = Maps.newHashMap();
    private final Map<Language, MessageEmbed> fullMessageEmbed = Maps.newHashMap();

    public HelpCommand(GiveawayBot bot) {
        super(bot, false, "ghelp");

        this.buildMessages(bot.getLanguageRegistry(), bot.getDefaults().getPalette());
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(server.canMemberManage(sender) ? this.fullMessageEmbed.get(server.getLanguage()) : this.limitedMessageEmbed.get(server.getLanguage())).queue();
    }

    private void buildMessages(LanguageRegistry languageRegistry, Palette palette) {
        for (Language language : Language.values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(languageRegistry.get(language, Text.HELP_EMBED_TITLE).get())
                    .setFooter(languageRegistry.get(language, Text.HELP_EMBED_FOOTER).get())
                    .setColor(palette.primary())
                    .addField("General Commands", languageRegistry.get(language, Text.HELP_LIMITED_SECTION).get(), false);
            this.limitedMessageEmbed.put(language, embedBuilder.build());
            embedBuilder.addField("Admin Commands",
                    languageRegistry.get(language, Text.HELP_ADMIN_SECTION).get(), false);
            this.fullMessageEmbed.put(language, embedBuilder.build());
        }
    }
}
