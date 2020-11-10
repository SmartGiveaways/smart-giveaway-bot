package pink.zak.giveawaybot.listener;

import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;

public class GiveawayDeletionListener extends ListenerAdapter {
    private final GiveawayCache giveawayCache;
    private final GiveawayController giveawayController;
    private final ServerCache serverCache;

    public GiveawayDeletionListener(GiveawayBot bot) {
        this.giveawayCache = bot.getGiveawayCache();
        this.giveawayController = bot.getGiveawayController();
        this.serverCache = bot.getServerCache();
    }

    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        this.giveawayCache.get(event.getMessageIdLong()).thenAccept(giveaway -> {
            if (giveaway != null) {
                GiveawayBot.getLogger().info("Giveaway {} in server {} was deleted", giveaway.messageId(), giveaway.serverId());
                this.giveawayController.deleteGiveaway(giveaway);
            }
        });
    }

    public void onTextChannelDelete(@NotNull TextChannelDeleteEvent event) {
        this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
            if (server.getActiveGiveaways().isEmpty()) {
                return;
            }
            for (CurrentGiveaway giveaway : this.giveawayCache.getMap().values()) {
                if (giveaway.channelId() == event.getChannel().getIdLong()) {
                    this.giveawayController.deleteGiveaway(giveaway);
                }
            }
        });
    }
}
