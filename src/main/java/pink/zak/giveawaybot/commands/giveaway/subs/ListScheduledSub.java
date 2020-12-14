package pink.zak.giveawaybot.commands.giveaway.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;
import java.util.UUID;

public class ListScheduledSub extends SubCommand {
    private final ScheduledGiveawayCache giveawayCache;
    private final Palette palette;

    public ListScheduledSub(GiveawayBot bot) {
        super(bot, true, true, false);
        this.addFlatWithAliases("list", "show");
        this.addFlat("scheduled");

        this.giveawayCache = bot.getScheduledGiveawayCache();
        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        if (server.getScheduledGiveaways().isEmpty()) {
            this.langFor(server, Text.NO_SCHEDULED_GIVEAWAYS).to(event.getChannel());
            return;
        }
        StringBuilder descriptionBuilder = new StringBuilder();
        for (UUID giveawayId : server.getScheduledGiveaways()) {
            ScheduledGiveaway giveaway = this.giveawayCache.getSync(giveawayId);
            descriptionBuilder.append("**")
                    .append(giveaway.giveawayItem())
                    .append("** [")
                    .append(giveaway.getStartFormatted())
                    .append("]")
                    .append(" -> (ID: ")
                    .append(giveaway.uuid())
                    .append(")\n");
        }
        event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle(this.langFor(server, Text.SCHEDULED_GIVEAWAY_LIST_EMBED_TITLE).get())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).get())
                .setDescription(descriptionBuilder.toString())
                .setColor(this.palette.primary())
                .build()).queue();
    }
}