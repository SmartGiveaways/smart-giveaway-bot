package pink.zak.giveawaybot.discord.commands.discord.giveaway.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.lang.Text;
import pink.zak.giveawaybot.discord.service.colour.Palette;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;

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
            CurrentGiveaway giveaway = this.giveawayCache.get(giveawayId);
            descriptionBuilder
                    .append("**")
                    .append(giveaway.getLinkedGiveawayItem())
                    .append("** -> (ID: ")
                    .append(giveaway.getMessageId())
                    .append(")\n");
        }
        event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle(this.langFor(server, Text.GIVEAWAY_LIST_EMBED_TITLE).toString())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).toString())
                .setDescription(descriptionBuilder.toString())
                .setColor(this.palette.primary())
                .build()).queue();
    }
}
