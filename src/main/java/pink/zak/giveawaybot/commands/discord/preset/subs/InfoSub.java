package pink.zak.giveawaybot.commands.discord.preset.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.Defaults;
import pink.zak.giveawaybot.data.models.Preset;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

import java.util.List;
import java.util.Map;

public class InfoSub extends SubCommand {
    private final Preset defaultPreset;
    private final Palette palette;

    public InfoSub(GiveawayBot bot) {
        super(bot, "info", false, false);
        this.defaultPreset = Defaults.defaultPreset;
        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        String presetName = event.getOption("presetname").getAsString();
        Preset preset = presetName.equalsIgnoreCase("default") ? this.defaultPreset : server.getPreset(presetName);
        if (preset == null) {
            this.langFor(server, Text.COULDNT_FIND_PRESET).to(event);
            return;
        }
        if (preset.getSettings().isEmpty()) {
            this.langFor(server, Text.PRESET_HAS_NO_SETTINGS, replacer -> replacer.set("preset", preset.getName())).to(event);
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Setting, Object> entry : preset.getSettings().entrySet()) {
            builder.append(entry.getKey().getName())
                    .append(" - ")
                    .append(entry.getValue())
                    .append("\n");
        }
        event.replyEmbeds(new EmbedBuilder()
                .setTitle(this.langFor(server, Text.PRESET_OPTIONS_LIST_EMBED_TITLE, replacer -> replacer.set("preset", preset.getName())).toString())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).toString())
                .setColor(this.palette.primary())
                .setDescription(builder.toString())
                .build()).queue();
    }
}
