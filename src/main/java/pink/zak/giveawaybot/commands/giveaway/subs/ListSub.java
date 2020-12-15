package pink.zak.giveawaybot.commands.giveaway.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public class ListSub extends SubCommand {
    private final Palette palette;
    private final GiveawayCache giveawayCache;

    public ListSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.addFlatWithAliases("list", "show");

        this.palette = bot.getDefaults().getPalette();
        this.giveawayCache = bot.getGiveawayCache();
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        if (server.getActiveGiveaways().isEmpty()) {
            this.langFor(server, Text.NO_ACTIVE_GIVEAWAYS).to(event.getChannel());
            return;
        }
        StringBuilder descriptionBuilder = new StringBuilder();
        for (long giveawayId : server.getActiveGiveaways()) {
            CurrentGiveaway giveaway = this.giveawayCache.getSync(giveawayId);
            descriptionBuilder.append("[**")
                    .append(giveaway.giveawayItem())
                    .append("**](")
                    .append(giveaway.messageLink())
                    .append(") -> (ID: ")
                    .append(giveaway.messageId())
                    .append(")\n");
        }
        event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle(this.langFor(server, Text.GIVEAWAY_LIST_EMBED_TITLE).get())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).get())
                .setDescription(descriptionBuilder.toString())
                .setColor(this.palette.primary())
                .build()).queue();
    }
}
