package pink.zak.giveawaybot.commands.discord.giveaway.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.data.cache.GiveawayCache;
import pink.zak.giveawaybot.data.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.data.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.data.models.giveaway.finished.FullFinishedGiveaway;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.BotSubCommand;
import pink.zak.giveawaybot.service.time.Time;

import java.util.UUID;
import java.util.stream.Collectors;

public class InfoSub extends BotSubCommand {
    private final ScheduledGiveawayCache scheduledGiveawayCache;
    private final FinishedGiveawayCache finishedGiveawayCache;
    private final GiveawayCache currentGiveawayCache;
    private final Palette palette;

    public InfoSub(GiveawayBot bot) {
        super(bot, "info", false, false);
        this.scheduledGiveawayCache = bot.getScheduledGiveawayCache();
        this.finishedGiveawayCache = bot.getFinishedGiveawayCache();
        this.currentGiveawayCache = bot.getGiveawayCache();
        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        String stringGiveawayId = event.getOption("giveawayid").getAsString();
        UUID uuid = null;
        long longId = -1L;
        try {
            if (stringGiveawayId.contains("-"))
                uuid = UUID.fromString(stringGiveawayId);
            else {
                longId = Long.parseLong(stringGiveawayId);
            }
        } catch (Exception ex) {
            this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(event, true);
            return;
        }
        if (uuid == null) {
            if (longId < 786066350882488381L) {
                this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(event, true);
                return;
            }
            if (server.getFinishedGiveaways().contains(longId)) {
                FullFinishedGiveaway giveaway = this.finishedGiveawayCache.get(longId);
                if (giveaway == null) {
                    this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(event, true);
                    return;
                }
                this.finishedGiveawayMessage(event, server, giveaway);
            } else if (server.getActiveGiveaways().contains(longId)) {
                CurrentGiveaway giveaway = this.currentGiveawayCache.get(longId);
                if (giveaway == null) {
                    this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(event, true);
                    return;
                }
                this.currentGiveawayMessage(event, server, giveaway);
            } else {
                this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(event, true);
            }
        } else if (server.getScheduledGiveaways().contains(uuid)) {

            ScheduledGiveaway giveaway = this.scheduledGiveawayCache.get(uuid);
            if (giveaway == null) {
                this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(event, true);
                return;
            }
            this.scheduledGiveawayMessage(event, server, giveaway);
        } else {
            this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(event, true);
        }
    }

    private void finishedGiveawayMessage(SlashCommandEvent event, Server server, FullFinishedGiveaway giveaway) {
        event.replyEmbeds(
            new EmbedBuilder()
                .setTitle(this.langFor(server, Text.FINISHED_GIVEAWAY_INFO_EMBED_TITLE).toString())
                .setDescription(this.langFor(server, Text.FINISHED_GIVEAWAY_INFO_EMBED_DESCRIPTION, replacer -> replacer
                    .set("id", giveaway.getMessageId())
                    .set("item", giveaway.getGiveawayItem())
                    .set("message_link", giveaway.getMessageLink())
                    .set("start_time", Time.formatAsDateTime(giveaway.getStartTime()) + " UTC")
                    .set("end_time", Time.formatAsDateTime(giveaway.getEndTime()) + " UTC")
                    .set("winners", giveaway.getWinners().stream().map(winnerId -> "<@" + winnerId + ">").collect(Collectors.joining(", ")))
                    .set("total_entries", giveaway.getTotalEntries().toString())).toString())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).toString())
                .setColor(this.palette.primary())
                .build()
        ).setEphemeral(true).queue();
    }

    private void currentGiveawayMessage(SlashCommandEvent event, Server server, CurrentGiveaway giveaway) {
        event.replyEmbeds(
            new EmbedBuilder()
                .setTitle(this.langFor(server, Text.CURRENT_GIVEAWAY_INFO_EMBED_TITLE).toString())
                .setDescription(this.langFor(server, Text.CURRENT_GIVEAWAY_INFO_EMBED_DESCRIPTION, replacer -> replacer
                    .set("id", giveaway.getMessageId())
                    .set("item", giveaway.getGiveawayItem())
                    .set("message_link", giveaway.getMessageLink())
                    .set("start_time", Time.formatAsDateTime(giveaway.getStartTime()) + " UTC")
                    .set("end_time", Time.formatAsDateTime(giveaway.getEndTime()) + " UTC")
                    .set("entered_users", giveaway.getEnteredUsers().size())).toString())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).toString())
                .setColor(this.palette.primary())
                .build()
        ).setEphemeral(true).queue();
    }

    private void scheduledGiveawayMessage(SlashCommandEvent event, Server server, ScheduledGiveaway giveaway) {
        event.replyEmbeds(
            new EmbedBuilder()
                .setTitle(this.langFor(server, Text.SCHEDULED_GIVEAWAY_INFO_EMBED_TITLE).toString())
                .setDescription(this.langFor(server, Text.SCHEDULED_GIVEAWAY_INFO_EMBED_DESCRIPTION, replacer -> replacer
                    .set("uuid", giveaway.getUuid())
                    .set("item", giveaway.getGiveawayItem())
                    .set("preset_name", giveaway.getPresetName())
                    .set("winner_amount", giveaway.getWinnerAmount())
                    .set("start_time", Time.formatAsDateTime(giveaway.getStartTime()) + " UTC")
                    .set("end_time", Time.formatAsDateTime(giveaway.getEndTime()) + " UTC")).toString())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).toString())
                .setColor(this.palette.primary())
                .build()
        ).setEphemeral(true).queue();
    }
}
