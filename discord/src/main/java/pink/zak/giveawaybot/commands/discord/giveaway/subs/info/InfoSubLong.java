package pink.zak.giveawaybot.commands.discord.giveaway.subs.info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.time.Time;
import pink.zak.giveawaybot.service.types.NumberUtils;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

import java.util.List;
import java.util.stream.Collectors;

public class InfoSubLong extends SubCommand {
    private final FinishedGiveawayCache finishedGiveawayCache;
    private final GiveawayCache currentGiveawayCache;
    private final Palette palette;

    public InfoSubLong(GiveawayBot bot) {
        super(bot, true, false, false);
        this.finishedGiveawayCache = bot.getFinishedGiveawayCache();
        this.currentGiveawayCache = bot.getGiveawayCache();
        this.palette = bot.getDefaults().getPalette();

        this.addFlat("info");
        this.addArgument(Long.class, NumberUtils::isLikelyLong); // Giveaway ID
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        long giveawayId = this.parseArgument(args, 1);
        TextChannel channel = event.getChannel();
        if (giveawayId < 786066350882488381L) {
            this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(channel);
            return;
        }
        if (server.getFinishedGiveaways().contains(giveawayId)) {
            FinishedGiveaway giveaway = this.finishedGiveawayCache.getSync(giveawayId);
            if (giveaway == null) {
                this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(channel);
                return;
            }
            this.finishedGiveawayMessage(channel, server, giveaway);
        } else if (server.getActiveGiveaways().contains(giveawayId)) {
            CurrentGiveaway giveaway = this.currentGiveawayCache.getSync(giveawayId);
            if (giveaway == null) {
                this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(channel);
                return;
            }
            this.currentGiveawayMessage(channel, server, giveaway);
        } else {
            this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(channel);
        }
    }

    private void finishedGiveawayMessage(TextChannel channel, Server server, FinishedGiveaway giveaway) {
        channel.sendMessage(
                new EmbedBuilder()
                        .setTitle(this.langFor(server, Text.FINISHED_GIVEAWAY_INFO_EMBED_TITLE).get())
                        .setDescription(this.langFor(server, Text.FINISHED_GIVEAWAY_INFO_EMBED_DESCRIPTION, replacer -> replacer
                                .set("id", giveaway.getMessageId())
                                .set("item", giveaway.getGiveawayItem())
                                .set("message_link", giveaway.getMessageLink())
                                .set("start_time", Time.formatAsDateTime(giveaway.getStartTime()) + " UTC")
                                .set("end_time", Time.formatAsDateTime(giveaway.getEndTime()) + " UTC")
                                .set("winners", giveaway.getWinners().stream().map(winnerId -> "<@" + winnerId + ">").collect(Collectors.joining(", ")))
                                .set("total_entries", giveaway.getTotalEntries().toString())).get())
                        .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).get())
                        .setColor(this.palette.primary())
                        .build()
        ).queue();
    }

    private void currentGiveawayMessage(TextChannel channel, Server server, CurrentGiveaway giveaway) {
        channel.sendMessage(
                new EmbedBuilder()
                        .setTitle(this.langFor(server, Text.CURRENT_GIVEAWAY_INFO_EMBED_TITLE).get())
                        .setDescription(this.langFor(server, Text.CURRENT_GIVEAWAY_INFO_EMBED_DESCRIPTION, replacer -> replacer
                                .set("id", giveaway.getMessageId())
                                .set("item", giveaway.getGiveawayItem())
                                .set("message_link", giveaway.getMessageLink())
                                .set("start_time", Time.formatAsDateTime(giveaway.getStartTime()) + " UTC")
                                .set("end_time", Time.formatAsDateTime(giveaway.getEndTime()) + " UTC")
                                .set("entered_users", giveaway.getEnteredUsers().size())).get())
                        .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).get())
                        .setColor(this.palette.primary())
                        .build()
        ).queue();
    }
}
