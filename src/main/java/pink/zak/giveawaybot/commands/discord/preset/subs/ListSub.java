package pink.zak.giveawaybot.commands.discord.preset.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Preset;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.BotSubCommand;

public class ListSub extends BotSubCommand {
    private final Palette palette;

    public ListSub(GiveawayBot bot) {
        super(bot, "list", false, false);
        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        StringBuilder listBuilder = new StringBuilder(this.langFor(server, Text.PRESET_LIST_DEFAULT_ENTRY).toString());
        for (Preset preset : server.getPresets().values()) {
            listBuilder.append("\n")
                .append(preset.getName());
        }
        event.replyEmbeds(new EmbedBuilder()
            .setColor(this.palette.primary())
            .setTitle(this.langFor(server, Text.PRESET_LIST_EMBED_TITLE, replacer -> replacer.set("preset-count", server.getPresets().size() + 1)).toString())
            .setDescription(listBuilder.toString())
            .build())
            .setEphemeral(true).queue();
    }
}
