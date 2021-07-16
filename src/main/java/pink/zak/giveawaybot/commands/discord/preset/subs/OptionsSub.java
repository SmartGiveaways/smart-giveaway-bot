package pink.zak.giveawaybot.commands.discord.preset.subs;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.lang.model.Language;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

import java.util.List;
import java.util.Map;

public class OptionsSub extends SubCommand {
    private final Palette palette;
    private final Map<String, MessageEmbed> optionsList;

    public OptionsSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.palette = bot.getDefaults().getPalette();
        this.optionsList = this.setupOptionsList();

        this.addFlatWithAliases("options", "settings");
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(this.optionsList.get(server.getLanguage())).queue();
    }

    private Map<String, MessageEmbed> setupOptionsList() {
        Map<String, MessageEmbed> messageEmbeds = Maps.newHashMap();
        StringBuilder builder = new StringBuilder();
        for (Language language : this.languageRegistry.languageMap().values()) {
            for (Setting setting : Setting.values()) {
                builder.append(setting.getPrimaryConfigName())
                        .append(" - ")
                        .append(language.getValue(setting.getDescription()).toString())
                        .append("\n");
            }
            messageEmbeds.put(language.getIdentifier(), new EmbedBuilder()
                    .setColor(this.palette.primary())
                    .setTitle(language.getValue(Text.PRESET_OPTIONS_LIST_OPTIONS_EMBED_TITLE).toString())
                    .setDescription(builder.toString())
                    .build());
        }
        return messageEmbeds;
    }
}
