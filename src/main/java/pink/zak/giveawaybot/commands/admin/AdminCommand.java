package pink.zak.giveawaybot.commands.admin;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.admin.subs.ListManagersSub;
import pink.zak.giveawaybot.commands.admin.subs.ManagerAddSub;
import pink.zak.giveawaybot.commands.admin.subs.ManagerRemoveSub;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.enums.Language;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;

import java.util.List;
import java.util.Map;

public class AdminCommand extends SimpleCommand {
    private final Map<Language, MessageEmbed> messageEmbeds = Maps.newHashMap();

    public AdminCommand(GiveawayBot bot) {
        super(bot, true, "gadmin");

        this.setAliases("gmanage", "gmng", "gadmn");
        this.setSubCommands(
                new ListManagersSub(bot),
                new ManagerAddSub(bot),
                new ManagerRemoveSub(bot)
        );
        this.buildMessages(bot.getLanguageRegistry(), bot.getDefaults().getPalette());
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        event.getTextChannel().sendMessage(this.messageEmbeds.get(server.getLanguage())).queue();
    }

    private void buildMessages(LanguageRegistry languageRegistry, Palette palette) {
        for (Language language : Language.values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(languageRegistry.get(language, Text.ADMIN_EMBED_TITLE).get())
                    .setFooter(languageRegistry.get(language, Text.GENERIC_EMBED_FOOTER).get())
                    .setDescription(languageRegistry.get(language, Text.ADMIN_EMBED_CONTENT).get())
                    .setColor(palette.primary());
            this.messageEmbeds.put(language, embedBuilder.build());
        }
    }
}
