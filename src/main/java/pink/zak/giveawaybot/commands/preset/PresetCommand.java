package pink.zak.giveawaybot.commands.preset;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.preset.subs.*;
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

public class PresetCommand extends SimpleCommand {
    private final LanguageRegistry languageRegistry;
    private final ThreadManager threadManager;
    private final Palette palette;
    private final Map<Language, MessageEmbed> embedMessages = Maps.newHashMap();

    public PresetCommand(GiveawayBot bot) {
        super(bot, "preset");

        this.setAliases("presets");
        this.setSubCommands(
                new CreateSub(bot),
                new DeleteSub(bot),
                new ListSub(bot),
                new OptionsSub(bot),
                new PresetOptionsSub(bot),
                new SetOptionSub(bot)
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
                    .setTitle(this.languageRegistry.get(language, Text.PRESET_EMBED_TITLE).get())
                    .setFooter(this.languageRegistry.get(language, Text.GENERIC_EMBED_FOOTER).get())
                    .setDescription(this.languageRegistry.get(language, Text.PRESET_EMBED_CONTENT).get())
                    .setColor(this.palette.primary());
            this.embedMessages.put(language, embedBuilder.build());
        }
    }
}
