package pink.zak.giveawaybot.discord.listener;

import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.cache.ServerCache;
import pink.zak.giveawaybot.discord.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.pipelines.giveaway.steps.DeletionStep;
import pink.zak.giveawaybot.discord.threads.ThreadFunction;

public class GiveawayDeletionListener extends ListenerAdapter {
    private final GiveawayCache giveawayCache;
    private final ServerCache serverCache;
    private final DeletionStep deletionStep;

    public GiveawayDeletionListener(GiveawayBot bot) {
        this.giveawayCache = bot.getGiveawayCache();
        this.serverCache = bot.getServerCache();
        this.deletionStep = new DeletionStep(bot);
    }

    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        this.giveawayCache.getAsync(event.getMessageIdLong(), ThreadFunction.GENERAL).thenAccept(giveaway -> {
            if (giveaway != null) {
                GiveawayBot.logger().info("Giveaway {} in server {} was deleted", giveaway.getMessageId(), giveaway.getServerId());
                this.deletionStep.delete(giveaway);
            }
        });
    }

    public void onTextChannelDelete(@NotNull TextChannelDeleteEvent event) {
        this.serverCache.getAsync(event.getGuild().getIdLong(), ThreadFunction.GENERAL).thenAccept(server -> {
            if (server.getActiveGiveaways().isEmpty()) {
                return;
            }
            // TODO can be simplified to only loop through a server's giveaways.
            for (CurrentGiveaway giveaway : this.giveawayCache.getMap().values()) {
                if (giveaway.getChannelId() == event.getChannel().getIdLong()) {
                    this.deletionStep.delete(giveaway);
                }
            }
        });
    }
}
