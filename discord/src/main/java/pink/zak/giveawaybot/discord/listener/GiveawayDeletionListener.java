package pink.zak.giveawaybot.discord.listener;

import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.controllers.ScheduledGiveawayController;
import pink.zak.giveawaybot.discord.data.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.data.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.discord.data.cache.ServerCache;
import pink.zak.giveawaybot.discord.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.data.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.discord.pipelines.giveaway.steps.DeletionStep;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.threads.ThreadFunction;

public class GiveawayDeletionListener extends ListenerAdapter {
    private final GiveawayCache giveawayCache;
    private final ScheduledGiveawayCache scheduledGiveawayCache;
    private final ScheduledGiveawayController scheduledGiveawayController;
    private final ServerCache serverCache;
    private final DeletionStep deletionStep;

    public GiveawayDeletionListener(GiveawayBot bot) {
        this.giveawayCache = bot.getGiveawayCache();
        this.scheduledGiveawayCache = bot.getScheduledGiveawayCache();
        this.scheduledGiveawayController = bot.getScheduledGiveawayController();
        this.serverCache = bot.getServerCache();
        this.deletionStep = new DeletionStep(bot);
    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        this.giveawayCache.getAsync(event.getMessageIdLong(), ThreadFunction.GENERAL).thenAccept(giveaway -> {
            if (giveaway != null) {
                JdaBot.logger.info("Giveaway {} in server {} was deleted", giveaway.getMessageId(), giveaway.getServerId());
                this.deletionStep.delete(giveaway);
            }
        });
    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        long channelId = event.getChannel().getIdLong();
        this.serverCache.getAsync(event.getGuild().getIdLong(), ThreadFunction.GENERAL).thenAccept(server -> {
            if (!server.getActiveGiveaways().isEmpty()) {
                for (CurrentGiveaway giveaway : this.giveawayCache.getMap().values()) {
                    if (giveaway.getChannelId() == channelId) {
                        this.deletionStep.delete(giveaway);
                    }
                }
            }
            if (!server.getScheduledGiveaways().isEmpty()) {
                for (ScheduledGiveaway giveaway : this.scheduledGiveawayCache.getMap().values()) {
                    if (giveaway.getChannelId() == channelId) {
                        this.scheduledGiveawayController.deleteGiveaway(server, giveaway);
                    }
                }
            }
        });
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        this.onUnavailableGuildLeave(new UnavailableGuildLeaveEvent(event.getJDA(), event.getResponseNumber(), event.getGuild().getIdLong()));
    }

    @Override
    public void onUnavailableGuildLeave(UnavailableGuildLeaveEvent event) {
        long guildId = event.getGuildIdLong();
        this.serverCache.getAsync(guildId, ThreadFunction.GENERAL).thenAccept(server -> {
            if (!server.getActiveGiveaways().isEmpty()) {
                for (CurrentGiveaway giveaway : this.giveawayCache.getMap().values()) {
                    if (giveaway.getServerId() == guildId) {
                        this.deletionStep.delete(giveaway);
                    }
                }
            }
            if (!server.getScheduledGiveaways().isEmpty()) {
                for (ScheduledGiveaway giveaway : this.scheduledGiveawayCache.getMap().values()) {
                    if (giveaway.getServerId() == guildId) {
                        this.scheduledGiveawayController.deleteGiveaway(server, giveaway);
                    }
                }
            }
        });
    }
}
