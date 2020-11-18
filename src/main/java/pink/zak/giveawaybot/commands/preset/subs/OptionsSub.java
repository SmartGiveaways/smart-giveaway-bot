package pink.zak.giveawaybot.commands.preset.subs;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.lang.enums.Language;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;
import java.util.Map;

public class OptionsSub extends SubCommand {
    private final Palette palette;
    private final Map<Language, MessageEmbed> optionsList;

    public OptionsSub(GiveawayBot bot) {
        super(bot);
        this.palette = bot.getDefaults().getPalette();
        this.optionsList = this.setupOptionsList();

        this.addFlatWithAliases("options", "settings");
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(this.optionsList.get(server.getLanguage())).queue();
    }

    private Map<Language, MessageEmbed> setupOptionsList() {
        Map<Language, MessageEmbed> messageEmbeds = Maps.newHashMap();
        StringBuilder builder = new StringBuilder();
        for (Language language : Language.values()) {
            for (Setting setting : Setting.values()) {
                builder.append(setting.getPrimaryConfigName())
                        .append(" - ")
                        .append(this.languageRegistry.get(language, setting.getDescription()).get())
                        .append("\n");
            }
            messageEmbeds.put(language, new EmbedBuilder()
                    .setColor(this.palette.primary())
                    .setTitle(this.languageRegistry.get(language, Text.PRESET_OPTIONS_LIST_OPTIONS_EMBED_TITLE).get())
                    .setDescription(builder.toString())
                    .build());
        }
        return messageEmbeds;
    }
}
