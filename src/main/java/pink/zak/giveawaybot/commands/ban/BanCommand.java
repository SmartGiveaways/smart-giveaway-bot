package pink.zak.giveawaybot.commands.ban;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.ban.subs.BanListSub;
import pink.zak.giveawaybot.commands.ban.subs.BanSub;
import pink.zak.giveawaybot.commands.ban.subs.ShadowBanSub;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.enums.Language;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;

import java.util.List;
import java.util.Map;

public class BanCommand extends SimpleCommand {
    private final Map<Language, MessageEmbed> messageEmbeds = Maps.newHashMap();

    public BanCommand(GiveawayBot bot) {
        super(bot, true, "gban");

        BanCmdUtils cmdUtils = new BanCmdUtils(bot);
        BanListSub banListSub = new BanListSub(bot);
        this.setSubCommands(
                banListSub,
                new BanSub(bot, cmdUtils),
                new ShadowBanSub(bot, cmdUtils)
        );
        bot.registerListeners(banListSub);
        this.buildMessages(bot.getLanguageRegistry(), bot.getDefaults().getPalette());
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(this.messageEmbeds.get(server.getLanguage())).queue();
    }

    private void buildMessages(LanguageRegistry languageRegistry, Palette palette) {
        for (Language language : Language.values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(languageRegistry.get(language, Text.BAN_EMBED_TITLE).get())
                    .setFooter(languageRegistry.get(language, Text.GENERIC_EMBED_FOOTER).get())
                    .setDescription(languageRegistry.get(language, Text.BAN_EMBED_CONTENT).get())
                    .setColor(palette.primary());
            this.messageEmbeds.put(language, embedBuilder.build());
        }
    }
}
