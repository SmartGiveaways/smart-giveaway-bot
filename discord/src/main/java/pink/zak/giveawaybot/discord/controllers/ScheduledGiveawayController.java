package pink.zak.giveawaybot.discord.controllers;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.discord.cache.ServerCache;
import pink.zak.giveawaybot.discord.defaults.Defaults;
import pink.zak.giveawaybot.discord.enums.ReturnCode;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.discord.service.tuple.ImmutablePair;
import pink.zak.giveawaybot.discord.storage.ScheduledGiveawayStorage;
import pink.zak.giveawaybot.discord.threads.ThreadFunction;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledGiveawayController {
    private final ScheduledExecutorService scheduler;
    private final ShardManager shardManager;
    private final GiveawayController giveawayController;
    private final ServerCache serverCache;
    private final ScheduledGiveawayStorage scheduledGiveawayStorage;
    private final ScheduledGiveawayCache scheduledGiveawayCache;
    private final Defaults defaults;

    public ScheduledGiveawayController(GiveawayBot bot) {
        this.scheduler = bot.getThreadManager().getScheduler();
        this.shardManager = bot.getShardManager();
        this.giveawayController = bot.getGiveawayController();
        this.serverCache = bot.getServerCache();
        this.scheduledGiveawayStorage = bot.getScheduledGiveawayStorage();
        this.scheduledGiveawayCache = bot.getScheduledGiveawayCache();
        this.defaults = bot.getDefaults();
        this.load();
    }

    public ImmutablePair<ScheduledGiveaway, ReturnCode> schedule(Server server, String presetName, long startTime, long endTime, TextChannel giveawayChannel, int winnerAmount, String giveawayItem) {
        if (server.getScheduledGiveaways().size() >= 10) {
            return ImmutablePair.of(null, ReturnCode.GIVEAWAY_LIMIT_FAILURE);
        }
        if (!giveawayChannel.getGuild().getSelfMember().hasPermission(giveawayChannel, this.defaults.getRequiredPermissions())) {
            return ImmutablePair.of(null, ReturnCode.PERMISSIONS_FAILURE);
        }
        if (!presetName.equalsIgnoreCase("default") && server.getPreset(presetName) == null) {
            return ImmutablePair.of(null, ReturnCode.NO_PRESET);
        }
        if (this.giveawayController.getGiveawayCountAt(server, startTime, endTime) >= 10) {
            return ImmutablePair.of(null, ReturnCode.FUTURE_GIVEAWAY_LIMIT_FAILURE);
        }
        ScheduledGiveaway giveaway = new ScheduledGiveaway(giveawayChannel.getIdLong(), server.getId(), startTime, endTime, winnerAmount, presetName, giveawayItem);
        this.scheduledGiveawayStorage.save(giveaway);
        this.scheduledGiveawayCache.set(giveaway.getUuid(), giveaway);
        server.getScheduledGiveaways().add(giveaway.getUuid());
        this.schedule(giveaway);
        return ImmutablePair.of(giveaway, ReturnCode.SUCCESS);
    }

    public void schedule(ScheduledGiveaway giveaway) {
        long waitTime = giveaway.getMillisToStart();
        this.scheduledGiveawayCache.addScheduledGiveaway(giveaway);
        if (waitTime <= 10000) {
            this.createCurrentGiveaway(giveaway);
        } else {
            this.scheduler.schedule(() -> this.createCurrentGiveaway(giveaway), waitTime, TimeUnit.MILLISECONDS);
        }
    }

    private void createCurrentGiveaway(ScheduledGiveaway giveaway) {
        this.serverCache.getAsync(giveaway.getServerId(), ThreadFunction.GENERAL).thenAccept(server -> {
            Guild guild = this.shardManager.getGuildById(server.getId());
            if (guild == null) {
                return;
            }
            TextChannel giveawayChannel = guild.getTextChannelById(giveaway.getChannelId());
            if (giveawayChannel == null) {
                return;
            }
            this.deleteGiveaway(server, giveaway);
            this.giveawayController.createGiveaway(
                    server, giveaway.getEndTime() - giveaway.getStartTime(), giveaway.getEndTime(), giveaway.getWinnerAmount(),
                    giveawayChannel, giveaway.getPresetName(), giveaway.getGiveawayItem()
            );
        });
    }

    public void deleteGiveaway(Server server, ScheduledGiveaway giveaway) {
        this.scheduledGiveawayCache.invalidate(giveaway.getUuid(), false);
        this.scheduledGiveawayStorage.delete(giveaway.getUuid());
        server.getScheduledGiveaways().remove(giveaway.getUuid());
    }

    private void load() {
        Set<ScheduledGiveaway> giveaways = this.scheduledGiveawayStorage.loadAll().join();
        Map<Long, Set<ScheduledGiveaway>> giveawaysToRemove = Maps.newHashMap();
        for (ScheduledGiveaway giveaway : giveaways) {
            if (System.currentTimeMillis() - giveaway.getEndTime() > 5000) {
                if (giveawaysToRemove.containsKey(giveaway.getServerId())) {
                    giveawaysToRemove.get(giveaway.getServerId()).add(giveaway);
                } else {
                    giveawaysToRemove.put(giveaway.getServerId(), Sets.newHashSet(giveaway));
                }
                continue;
            }
            this.schedule(giveaway);
        }
        for (long serverId : giveawaysToRemove.keySet()) {
            this.serverCache.getAsync(serverId, ThreadFunction.GENERAL).thenAccept(server -> {
                for (ScheduledGiveaway giveaway : giveawaysToRemove.get(serverId)) {
                    this.deleteGiveaway(server, giveaway);
                }
            });
        }
    }
}
