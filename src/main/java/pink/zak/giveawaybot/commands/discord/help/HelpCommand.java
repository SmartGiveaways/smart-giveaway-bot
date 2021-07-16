package pink.zak.giveawaybot.commands.discord.help;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.lang.model.Language;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.service.config.Reloadable;

import java.util.List;
import java.util.Map;

public class HelpCommand extends SimpleCommand implements Reloadable {
    private Map<String, MessageEmbed> limitedMessageEmbed = Maps.newHashMap();
    private Map<String, MessageEmbed> fullMessageEmbed = Maps.newHashMap();

    public HelpCommand(GiveawayBot bot) {
        super(bot, "ghelp", false, false);
        this.setAliases("gh");

        this.buildMessages(bot.getDefaults().getPalette());
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(server.canMemberManage(sender) ? this.fullMessageEmbed.get(server.getLanguage()) : this.limitedMessageEmbed.get(server.getLanguage())).queue();
    }

    private void buildMessages(Palette palette) {
        Map<String, MessageEmbed> limitedMessages = Maps.newHashMap();
        Map<String, MessageEmbed> fullMessages = Maps.newHashMap();
        for (Language language : this.languageRegistry.languageMap().values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(language.getValue(Text.HELP_EMBED_TITLE).toString())
                    .setFooter(language.getValue(Text.HELP_EMBED_FOOTER).toString())
                    .setColor(palette.primary())
                    .setDescription(language.getValue(Text.GENERIC_COMMAND_USAGE_EXAMPLE).replace(replacer -> replacer.set("command", "command")).toString())
                    .addField("General Commands", language.getValue(Text.HELP_LIMITED_SECTION).toString(), false);
            limitedMessages.put(language.getIdentifier(), embedBuilder.build());
            embedBuilder.addField("Admin Commands",
                    language.getValue(Text.HELP_ADMIN_SECTION).toString(), false);
            fullMessages.put(language.getIdentifier(), embedBuilder.build());
        }
        this.limitedMessageEmbed = limitedMessages;
        this.fullMessageEmbed = fullMessages;
    }

    @Override
    public void reload(GiveawayBot bot) {
        this.buildMessages(bot.getDefaults().getPalette());
    }
}
