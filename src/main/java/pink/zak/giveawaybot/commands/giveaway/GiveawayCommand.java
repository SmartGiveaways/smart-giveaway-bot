package pink.zak.giveawaybot.commands.giveaway;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.giveaway.subs.DeleteSub;
import pink.zak.giveawaybot.commands.giveaway.subs.ListSub;
import pink.zak.giveawaybot.commands.giveaway.subs.RerollSub;
import pink.zak.giveawaybot.commands.giveaway.subs.create.CreateSub;
import pink.zak.giveawaybot.commands.giveaway.subs.create.CreateWithChannelSub;
import pink.zak.giveawaybot.commands.giveaway.subs.schedule.ScheduleSub;
import pink.zak.giveawaybot.commands.giveaway.subs.schedule.ScheduleWithChannelSub;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.enums.Language;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;

import java.util.List;
import java.util.Map;

public class GiveawayCommand extends SimpleCommand {
    private final Map<Language, MessageEmbed> embedMessages = Maps.newHashMap();

    public GiveawayCommand(GiveawayBot bot) {
        super(bot, true, "giveaway");
        GiveawayCmdUtils cmdUtils = new GiveawayCmdUtils(bot);
        this.setAliases("g");

        this.setSubCommands(
                new CreateSub(bot, cmdUtils),
                new CreateWithChannelSub(bot, cmdUtils),
                new ScheduleSub(bot, cmdUtils),
                new ScheduleWithChannelSub(bot, cmdUtils),
                new DeleteSub(bot),
                new ListSub(bot),
                new RerollSub(bot)
        );

        this.buildMessages(bot.getLanguageRegistry(), bot.getDefaults().getPalette());
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(this.embedMessages.get(server.getLanguage())).queue();
    }

    private void buildMessages(LanguageRegistry languageRegistry, Palette palette) {
        for (Language language : Language.values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(languageRegistry.get(language, Text.GIVEAWAY_HELP_EMBED_TITLE).get())
                    .setFooter(languageRegistry.get(language, Text.GIVEAWAY_HELP_EMBED_FOOTER).get())
                    .setDescription(languageRegistry.get(language, Text.GIVEAWAY_HELP_EMBED_CONTENT).get())
                    .setColor(palette.primary());
            this.embedMessages.put(language, embedBuilder.build());
        }
    }
}
