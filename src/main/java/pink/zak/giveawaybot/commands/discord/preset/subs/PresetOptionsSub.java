package pink.zak.giveawaybot.commands.discord.preset.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

import java.util.List;
import java.util.Map;

public class PresetOptionsSub extends SubCommand {
    private final Preset defaultPreset;
    private final Palette palette;

    public PresetOptionsSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.defaultPreset = bot.getDefaults().getDefaultPreset();
        this.palette = bot.getDefaults().getPalette();

        this.addFlatWithAliases("settings", "setting", "options", "option");
        this.addArgument(String.class);
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        String presetName = this.parseArgument(args, event.getGuild(), 1);
        Preset preset = presetName.equalsIgnoreCase("default") ? this.defaultPreset : server.preset(presetName);
        if (preset == null) {
            this.langFor(server, Text.COULDNT_FIND_PRESET).to(event.getChannel());
            return;
        }
        if (preset.settings().isEmpty()) {
            this.langFor(server, Text.PRESET_HAS_NO_SETTINGS, replacer -> replacer.set("preset", preset.name())).to(event.getChannel());
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Setting, Object> entry : preset.settings().entrySet()) {
            builder.append(entry.getKey().getPrimaryConfigName())
                    .append(" - ")
                    .append(entry.getValue())
                    .append("\n");
        }
        event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle(this.langFor(server, Text.PRESET_OPTIONS_LIST_EMBED_TITLE, replacer -> replacer.set("preset", preset.name())).get())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).get())
                .setColor(this.palette.primary())
                .setDescription(builder.toString())
                .build()).queue();
    }
}
