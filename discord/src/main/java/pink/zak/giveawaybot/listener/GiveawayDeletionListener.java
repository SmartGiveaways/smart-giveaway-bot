package pink.zak.giveawaybot.listener;

import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.pipelines.giveaway.steps.DeletionStep;

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
        this.giveawayCache.get(event.getMessageIdLong()).thenAccept(giveaway -> {
            if (giveaway != null) {
                GiveawayBot.logger().info("Giveaway {} in server {} was deleted", giveaway.getMessageId(), giveaway.getServerId());
                this.deletionStep.delete(giveaway);
            }
        });
    }

    public void onTextChannelDelete(@NotNull TextChannelDeleteEvent event) {
        this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
            if (server.getActiveGiveaways().isEmpty()) {
                return;
            }
            for (CurrentGiveaway giveaway : this.giveawayCache.getMap().values()) {
                if (giveaway.getChannelId() == event.getChannel().getIdLong()) {
                    this.deletionStep.delete(giveaway);
                }
            }
        });
    }
}
