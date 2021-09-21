package pink.zak.giveawaybot.commands.discord.entries;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.cache.GiveawayCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.data.models.User;
import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.SlashCommandUtils;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.BotCommand;

import java.math.BigInteger;
import java.util.List;

public class EntriesCommand extends BotCommand {
    private final GiveawayCache giveawayCache;
    private final Palette palette;

    public EntriesCommand(GiveawayBot bot) {
        super(bot, "entries", false, false);
        this.giveawayCache = bot.getGiveawayCache();
        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        boolean self = !SlashCommandUtils.hasOption(event, "user");
        Member target = self ? sender : event.getOption("user").getAsMember();
        String targetName = target.getUser().getAsTag();
        if (server.getActiveGiveaways().isEmpty()) {
            this.langFor(server, Text.NO_ACTIVE_GIVEAWAYS).to(event, true);
            return;
        }
        User user = server.getUserCache().get(target.getIdLong());
        if (user.isBanned()) {
            this.langFor(server, self ? Text.SELF_BANNED_FROM_GIVEAWAYS : Text.TARGET_BANNED_FROM_GIVEAWAYS, replacer -> replacer.set("target", target.getAsMention())).to(event, true);
            return;
        }
        List<Long> presentGiveaways = server.getActiveGiveaways(user);
        if (presentGiveaways.isEmpty()) {
            this.langFor(server, self ? Text.SELF_NOT_ENTERED : Text.TARGET_NOT_ENTERED, replacer -> replacer.set("target", target.getAsMention())).to(event, true);
            return;
        }
        StringBuilder descriptionBuilder = new StringBuilder();
        for (long giveawayId : presentGiveaways) {
            BigInteger entries = user.getEntries(giveawayId);
            CurrentGiveaway giveaway = this.giveawayCache.get(giveawayId);
            if (giveaway != null) {
                descriptionBuilder.append(this.langFor(server,
                    entries.compareTo(BigInteger.ONE) < 1 ? Text.ENTRIES_EMBED_GIVEAWAY_LINE : Text.ENTRIES_EMBED_GIVEAWAY_LINE_PLURAL, replacer -> replacer
                        .set("item", giveaway.getLinkedGiveawayItem())
                        .set("entries", entries.toString())).toString());
            }
        }
        event.replyEmbeds(new EmbedBuilder()
            .setTitle(this.langFor(server, Text.ENTRIES_EMBED_TITLE, replacer -> replacer.set("target", targetName)).toString())
            .setColor(this.palette.primary())
            .setDescription(descriptionBuilder.toString())
            .build())
            .setEphemeral(true).queue();
    }

    @Override
    protected CommandData createCommandData() {
        return new CommandData("entries", "Get how many giveaway entries you have")
            .addOption(OptionType.USER, "user", "Get how many giveaway entries another user has", false);
    }
}
