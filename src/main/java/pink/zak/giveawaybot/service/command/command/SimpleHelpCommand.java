package pink.zak.giveawaybot.service.command.command;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.enums.Language;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.config.Reloadable;

import java.util.List;
import java.util.Map;

public abstract class SimpleHelpCommand extends SimpleCommand implements Reloadable {
    private Map<Language, MessageEmbed> languageMessages;
    private final Palette palette;

    private Text title;
    private Text footer;
    private Text description;

    public SimpleHelpCommand(GiveawayBot bot, String command, boolean manager, boolean premium) {
        super(bot, command, manager, premium);
        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(this.languageMessages.get(server.getLanguage())).queue();
    }

    public void setupMessages(Text title, Text footer, Text description) {
        this.title = title;
        this.footer = footer;
        this.description = description;
        this.buildMessages();
    }

    private void buildMessages() {
        Map<Language, MessageEmbed> languageMessages = Maps.newHashMap();
        for (Language language : Language.values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(this.languageRegistry.get(language, title).get())
                    .setFooter(this.languageRegistry.get(language, footer).get())
                    .setColor(this.palette.primary())
                    .setDescription(this.languageRegistry.get(language, Text.GENERIC_COMMAND_USAGE_EXAMPLE, replacer -> replacer.set("command", this.getCommand()))
                            + this.languageRegistry.get(language, description).get());
            languageMessages.put(language, embedBuilder.build());
        }
        this.languageMessages = languageMessages;
    }

    public void setupMessages(Text title, Text description) {
        this.setupMessages(title, Text.GENERIC_EMBED_FOOTER, description);
    }

    @Override
    public void reload(GiveawayBot bot) {
        this.buildMessages();
    }
}
