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

import java.util.List;
import java.util.Map;

public abstract class SimpleHelpCommand extends SimpleCommand {
    private final Map<Language, MessageEmbed> languageMessages = Maps.newHashMap();
    private final Palette palette;

    public SimpleHelpCommand(GiveawayBot bot, String command, boolean manager, boolean premium) {
        super(bot, command, manager, premium);
        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(this.languageMessages.get(server.getLanguage())).queue();
    }

    public void buildMessages(Text title, Text footer, Text description) {
        for (Language language : Language.values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(this.languageRegistry.get(language, title).get())
                    .setFooter(this.languageRegistry.get(language, footer).get())
                    .setColor(this.palette.primary())
                    .setDescription(this.languageRegistry.get(language, Text.GENERIC_COMMAND_USAGE_EXAMPLE, replacer -> replacer.set("command", this.getCommand()))
                            + this.languageRegistry.get(language, description).get());
            this.languageMessages.put(language, embedBuilder.build());
        }
    }

    public void buildMessages(Text title, Text description) {
        this.buildMessages(title, Text.GENERIC_EMBED_FOOTER, description);
    }
}
