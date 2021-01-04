package pink.zak.giveawaybot.commands.discord.giveaway.subs.info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.time.Time;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

import java.util.List;
import java.util.UUID;

public class InfoSubUuid extends SubCommand {
    private final ScheduledGiveawayCache scheduledGiveawayCache;
    private final Palette palette;

    public InfoSubUuid(GiveawayBot bot) {
        super(bot, true, false, false);
        this.scheduledGiveawayCache = bot.getScheduledGiveawayCache();
        this.palette = bot.getDefaults().getPalette();

        this.addFlat("info");
        this.addArgument(String.class, str -> str.length() == 36); // uuid
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        TextChannel channel = event.getChannel();
        UUID uuid;
        try {
            uuid = UUID.fromString(this.parseArgument(args, 1));
        } catch (IllegalArgumentException ex) {
            this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(channel);
            return;
        }
        ScheduledGiveaway giveaway = this.scheduledGiveawayCache.getSync(uuid);
        if (giveaway == null) {
            this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(channel);
            return;
        }
        event.getChannel().sendMessage(
                new EmbedBuilder()
                        .setTitle(this.langFor(server, Text.SCHEDULED_GIVEAWAY_INFO_EMBED_TITLE).get())
                        .setDescription(this.langFor(server, Text.SCHEDULED_GIVEAWAY_INFO_EMBED_DESCRIPTION, replacer -> replacer
                                .set("uuid", giveaway.getUuid())
                                .set("item", giveaway.getGiveawayItem())
                                .set("preset_name", giveaway.getPresetName())
                                .set("winner_amount", giveaway.getWinnerAmount())
                                .set("start_time", Time.formatAsDateTime(giveaway.getStartTime()) + " UTC")
                                .set("end_time", Time.formatAsDateTime(giveaway.getEndTime()) + " UTC")).get())
                        .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).get())
                        .setColor(this.palette.primary())
                        .build()
        ).queue();
    }
}
