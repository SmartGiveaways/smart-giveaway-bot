package pink.zak.giveawaybot.commands.preset.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public class OptionsSub extends SubCommand {
    private final Palette palette;
    private final MessageEmbed optionsList;

    public OptionsSub(GiveawayBot bot) {
        super(bot);
        this.palette = bot.getDefaults().getPalette();
        this.optionsList = this.setupOptionsList();

        this.addFlatWithAliases("options", "settings");
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(this.optionsList).queue();
    }

    private MessageEmbed setupOptionsList() {
        StringBuilder builder = new StringBuilder();
        for (Setting setting : Setting.values()) {
            builder.append(setting.getPrimaryConfigName())
                    .append(" - ")
                    .append(setting.getDescription())
                    .append("\n");
        }
        return new EmbedBuilder()
                .setColor(this.palette.primary())
                .setTitle("Available Options")
                .setDescription(builder.toString())
                .build();
    }
}
