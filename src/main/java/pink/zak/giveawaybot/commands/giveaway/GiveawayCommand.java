package pink.zak.giveawaybot.commands.giveaway;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.giveaway.subs.CreateSub;
import pink.zak.giveawaybot.commands.giveaway.subs.CreateWithChannelSub;
import pink.zak.giveawaybot.commands.giveaway.subs.ListSub;
import pink.zak.giveawaybot.commands.giveaway.subs.RerollSub;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.enums.Language;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;
import pink.zak.giveawaybot.threads.ThreadManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GiveawayCommand extends SimpleCommand {
    private final LanguageRegistry languageRegistry;
    private final ThreadManager threadManager;
    private final Palette palette;
    private final Map<Language, MessageEmbed> embedMessages = Maps.newHashMap();

    public GiveawayCommand(GiveawayBot bot) {
        super(bot, true, "giveaway");
        GiveawayCmdUtils cmdUtils = new GiveawayCmdUtils(bot);
        this.setAliases("g");

        this.setSubCommands(
                new CreateSub(bot, cmdUtils),
                new CreateWithChannelSub(bot, cmdUtils),
                new ListSub(bot),
                new RerollSub(bot)
        );

        this.languageRegistry = bot.getLanguageRegistry();
        this.threadManager = bot.getThreadManager();
        this.palette = bot.getDefaults().getPalette();
        this.buildMessages();
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(this.embedMessages.get(server.getLanguage())).queue(embed -> {
            embed.delete().queueAfter(60, TimeUnit.SECONDS, null, this.bot.getDeleteFailureThrowable(), this.threadManager.getScheduler());
            event.getMessage().delete().queueAfter(60, TimeUnit.SECONDS, null, this.bot.getDeleteFailureThrowable(), this.threadManager.getScheduler());
        });
    }

    private void buildMessages() {
        for (Language language : Language.values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(this.languageRegistry.get(language, Text.GIVEAWAY_EMBED_TITLE).get())
                    .setFooter(this.languageRegistry.get(language, Text.GIVEAWAY_EMBED_FOOTER).get())
                    .setDescription(this.languageRegistry.get(language, Text.GIVEAWAY_EMBED_CONTENT).get())
                    .setColor(this.palette.primary());
            this.embedMessages.put(language, embedBuilder.build());
        }
    }
}
