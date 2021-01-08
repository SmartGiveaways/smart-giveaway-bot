package pink.zak.giveawaybot.discord.commands.discord.preset.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.colour.Palette;
import pink.zak.giveawaybot.discord.lang.enums.Text;
import pink.zak.giveawaybot.discord.models.Preset;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;

import java.util.List;

public class ListSub extends SubCommand {
    private final Palette palette;

    public ListSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.palette = bot.getDefaults().getPalette();

        this.addFlat("list");
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        StringBuilder listBuilder = new StringBuilder(this.langFor(server, Text.PRESET_LIST_DEFAULT_ENTRY).get());
        for (Preset preset : server.getPresets().values()) {
            listBuilder.append("\n")
                    .append(preset.getName());
        }
        event.getChannel().sendMessage(new EmbedBuilder()
                .setColor(this.palette.primary())
                .setTitle(this.langFor(server, Text.PRESET_LIST_EMBED_TITLE, replacer -> replacer.set("preset-count", server.getPresets().size() + 1)).get())
                .setDescription(listBuilder.toString())
                .build()).queue();
    }
}
