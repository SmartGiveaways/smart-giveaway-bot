package pink.zak.giveawaybot.service.command.command;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.lang.model.Language;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.config.Reloadable;
import pink.zak.giveawaybot.service.text.Replace;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class SimpleHelpCommand extends SimpleCommand implements Reloadable {
    private Map<String, MessageEmbed> languageMessages;
    private final Palette palette;

    private Text title;
    private Text footer;
    private Text description;
    private Function<Language, Replace> replace;

    public SimpleHelpCommand(GiveawayBot bot, String command, boolean manager, boolean premium) {
        super(bot, command, manager, premium);
        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(this.languageMessages.get(server.getLanguage())).queue();
    }

    public void setupMessages(Text title, Text footer, Text description, Function<Language, Replace> replace) {
        this.title = title;
        this.footer = footer;
        this.description = description;
        this.replace = replace;
        this.buildMessages();
    }

    public void setupMessages(Text title, Text footer, Text description) {
        this.setupMessages(title, footer, description, language -> replacer -> replacer);
    }

    public void setupMessages(Text title, Text description) {
        this.setupMessages(title, Text.GENERIC_EMBED_FOOTER, description);
    }

    private void buildMessages() {
        Map<String, MessageEmbed> languageMessages = Maps.newHashMap();
        for (Language language : this.languageRegistry.languageMap().values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(language.getValue(this.title).get())
                    .setFooter(language.getValue(this.footer).get())
                    .setColor(this.palette.primary())
                    .setDescription(language.getValue(Text.GENERIC_COMMAND_USAGE_EXAMPLE).replace(replacer -> replacer.set("command", this.getCommand())).toString().concat(
                            language.getValue(this.description).replace(this.replace.apply(language)).get()));
            languageMessages.put(language.getIdentifier(), embedBuilder.build());
        }
        this.languageMessages = languageMessages;
    }

    @Override
    public void reload(GiveawayBot bot) {
        this.buildMessages();
    }
}
