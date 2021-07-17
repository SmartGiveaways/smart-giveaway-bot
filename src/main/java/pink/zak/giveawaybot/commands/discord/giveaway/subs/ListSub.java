package pink.zak.giveawaybot.commands.discord.giveaway.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.cache.GiveawayCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

public class ListSub extends SubCommand {
    private final Palette palette;
    private final GiveawayCache giveawayCache;

    public ListSub(GiveawayBot bot) {
        super(bot, "list", "current", true, false);

        this.palette = bot.getDefaults().getPalette();
        this.giveawayCache = bot.getGiveawayCache();
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        if (server.getActiveGiveaways().isEmpty()) {
            this.langFor(server, Text.NO_ACTIVE_GIVEAWAYS).to(event);
            return;
        }
        StringBuilder descriptionBuilder = new StringBuilder();
        for (long giveawayId : server.getActiveGiveaways()) {
            CurrentGiveaway giveaway = this.giveawayCache.get(giveawayId);
            descriptionBuilder
                    .append("**")
                    .append(giveaway.getLinkedGiveawayItem())
                    .append("** -> (ID: ")
                    .append(giveaway.getMessageId())
                    .append(")\n");
        }
        event.replyEmbeds(new EmbedBuilder()
                .setTitle(this.langFor(server, Text.GIVEAWAY_LIST_EMBED_TITLE).toString())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).toString())
                .setDescription(descriptionBuilder.toString())
                .setColor(this.palette.primary())
                .build()).queue();
    }
}
