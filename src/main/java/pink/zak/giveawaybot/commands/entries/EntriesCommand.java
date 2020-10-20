package pink.zak.giveawaybot.commands.entries;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.models.Giveaway;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class EntriesCommand extends SimpleCommand {
    private final ServerCache serverCache;
    private final GiveawayCache giveawayCache;
    private final Palette palette;
    private final Consumer<Throwable> deleteFailureThrowable;

    public EntriesCommand(GiveawayBot bot) {
        super(bot, false, "entries");
        this.serverCache = bot.getServerCache();
        this.giveawayCache = bot.getGiveawayCache();
        this.palette = bot.getDefaults().getPalette();
        this.deleteFailureThrowable = bot.getDeleteFailureThrowable();
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        TextChannel channel = event.getTextChannel();
        this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
            if (server.getActiveGiveaways().isEmpty()) {
                channel.sendMessage(":x: There are no active giveaways in this server.").queue();
                return;
            }
            Set<UUID> presentGiveaways = Sets.newHashSet();
            server.getUserCache().get(event.getAuthor().getIdLong()).thenAccept(user -> {
                for (UUID giveawayId : server.getActiveGiveaways().values()) {
                    if (user.entries().containsKey(giveawayId) && user.hasEntries(giveawayId)) {
                        presentGiveaways.add(giveawayId);
                    }
                }
                if (presentGiveaways.isEmpty()) {
                    channel.sendMessage(":x: You are not entered into any giveaways.").queue(message -> {
                        message.delete().queueAfter(10, TimeUnit.SECONDS, null, this.deleteFailureThrowable);
                    });
                    return;
                }
                StringBuilder descriptionBuilder = new StringBuilder();
                for (UUID giveawayId : presentGiveaways) {
                    BigInteger entries = user.getEntries(giveawayId);
                    Giveaway giveaway = this.giveawayCache.getSync(giveawayId);
                    if (giveaway != null) {
                        descriptionBuilder.append("**" + giveaway.giveawayItem() + "** -> " + entries + " entr" + (entries.compareTo(BigInteger.ONE) < 1 ? "y" : "ies") + "\n");
                    }
                }
                channel.sendMessage(new EmbedBuilder()
                        .setTitle("Giveaway Entries for " + sender.getEffectiveName() + "#" + sender.getUser().getDiscriminator())
                        .setColor(this.palette.primary())
                        .setDescription(descriptionBuilder.toString())
                        .build()).queue();
            }).exceptionally(ex -> {
                GiveawayBot.getLogger().error("", ex);
                return null;
            });
        }).exceptionally(ex -> {
            GiveawayBot.getLogger().error("", ex);
            return null;
        });
    }
}
