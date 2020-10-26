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
import pink.zak.giveawaybot.service.command.command.SubCommand;
import pink.zak.giveawaybot.service.types.UserUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
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

        this.setSubCommands(
                new UserEntriesSub(bot)
        );
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        this.runLogic(sender, event.getTextChannel(), true);
    }

    private class UserEntriesSub extends SubCommand {

        public UserEntriesSub(GiveawayBot bot) {
            super(bot, true);

            this.addArgument(Member.class); // target
        }

        @Override
        public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
            Member target = this.parseArgument(args, event.getGuild(), 0);
            if (target == null) {
                event.getChannel().sendMessage(":x: Couldn't find that member :worried:").queue();
                return;
            }
            runLogic(target, event.getTextChannel(), false);
        }
    }

    private void runLogic(Member target, TextChannel channel, boolean self) {
        String targetDisplay = self ? "You are " : UserUtils.getNameDiscrim(target) + " is ";
        this.serverCache.get(channel.getGuild().getIdLong()).thenAccept(server -> {
            if (server.getActiveGiveaways().isEmpty()) {
                channel.sendMessage(":x: There are no active giveaways in this server.").queue();
                return;
            }
            server.getUserCache().get(target.getIdLong()).thenAccept(user -> {
                if (user.isBanned()) {
                    channel.sendMessage(":x: " + targetDisplay + " banned from giveaways.").queue(message -> {
                        message.delete().queueAfter(10, TimeUnit.SECONDS, null, this.deleteFailureThrowable);
                    });
                    return;
                }
                Set<Long> presentGiveaways = Sets.newHashSet();
                for (long giveawayId : server.getActiveGiveaways()) {
                    if (user.entries().containsKey(giveawayId) && user.hasEntries(giveawayId)) {
                        presentGiveaways.add(giveawayId);
                    }
                }
                if (presentGiveaways.isEmpty()) {
                    channel.sendMessage(":x: " + targetDisplay + "not entered into any giveaways.").queue(message -> {
                        message.delete().queueAfter(10, TimeUnit.SECONDS, null, this.deleteFailureThrowable);
                    });
                    return;
                }
                StringBuilder descriptionBuilder = new StringBuilder();
                for (long giveawayId : presentGiveaways) {
                    BigInteger entries = user.entries(giveawayId);
                    Giveaway giveaway = this.giveawayCache.getSync(giveawayId);
                    if (giveaway != null) {
                        descriptionBuilder.append("**" + giveaway.giveawayItem() + "** -> " + entries + " entr" + (entries.compareTo(BigInteger.ONE) < 1 ? "y" : "ies") + "\n");
                    }
                }
                channel.sendMessage(new EmbedBuilder()
                        .setTitle("Giveaway Entries for " + UserUtils.getNameDiscrim(target))
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
