package pink.zak.giveawaybot.commands.preset.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;
import java.util.Map;

public class PresetOptionsSub extends SubCommand {
    private final ServerCache serverCache;
    private final Preset defaultPreset;
    private final Palette palette;

    public PresetOptionsSub(GiveawayBot bot) {
        super(bot);
        this.serverCache = bot.getServerCache();
        this.defaultPreset = bot.getDefaults().getDefaultPreset();
        this.palette = bot.getDefaults().getPalette();

        this.addFlatWithAliases("settings", "setting", "options", "option");
        this.addArgument(String.class);
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
            String presetName = this.parseArgument(args, event.getGuild(), 1);
            Preset preset = presetName.equalsIgnoreCase("default") ? this.defaultPreset : server.getPreset(presetName);
            if (preset == null) {
                event.getChannel().sendMessage(":x: Could not find a preset called ".concat(presetName)).queue();
                return;
            }
            if (preset.settings().isEmpty()) {
                event.getChannel().sendMessage(":x: The " + preset.name() + " preset has no settings. Use `>preset settings` to view available settings and `>preset set <preset> <setting> <value>` to set it.").queue();
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
                    .setColor(this.palette.primary())
                    .setDescription(builder.toString())
                    .setTitle("Settings for the " + preset.name() + " preset:")
                    .build()).queue();
        });
    }
}
