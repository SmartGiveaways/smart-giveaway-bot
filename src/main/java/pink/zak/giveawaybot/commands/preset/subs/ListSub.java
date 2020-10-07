package pink.zak.giveawaybot.commands.preset.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.awt.*;

public class ListSub extends SubCommand {
    private final ServerCache serverCache;

    public ListSub(GiveawayBot bot) {
        super(bot);
        this.serverCache = bot.getServerCache();

        this.addFlat("list");
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, String[] args) {
        this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
            StringBuilder listBuilder = new StringBuilder("default - This cannot be removed");
            for (Preset preset : server.getPresets().values()) {
                listBuilder.append("\n")
                        .append(preset.name());
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(Color.PINK)
                    .setTitle("Current Presets (**" + server.getPresets().size() + "**)")
                    .setDescription(listBuilder.toString())
                    .build()).queue();
        });
    }
}
