package pink.zak.giveawaybot.commands.discord.giveaway.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.data.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

import java.util.UUID;

public class ListScheduledSub extends SubCommand {
    private final ScheduledGiveawayCache giveawayCache;
    private final Palette palette;

    public ListScheduledSub(GiveawayBot bot) {
        super(bot, "list", "scheduled", true, false);

        this.giveawayCache = bot.getScheduledGiveawayCache();
        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        if (server.getScheduledGiveaways().isEmpty()) {
            this.langFor(server, Text.NO_SCHEDULED_GIVEAWAYS).to(event, true);
            return;
        }
        StringBuilder descriptionBuilder = new StringBuilder();
        for (UUID giveawayId : server.getScheduledGiveaways()) {
            ScheduledGiveaway giveaway = this.giveawayCache.get(giveawayId);
            descriptionBuilder.append("**")
                .append(giveaway.getGiveawayItem())
                .append("** [")
                .append(giveaway.getStartFormatted())
                .append("]")
                .append(" -> (ID: ")
                .append(giveaway.getUuid())
                .append(")\n");
        }
        event.replyEmbeds(new EmbedBuilder()
            .setTitle(this.langFor(server, Text.SCHEDULED_GIVEAWAY_LIST_EMBED_TITLE).toString())
            .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).toString())
            .setDescription(descriptionBuilder.toString())
            .setColor(this.palette.primary())
            .build())
            .setEphemeral(true).queue();
    }
}
