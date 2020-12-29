package pink.zak.giveawaybot.controllers;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.defaults.Defaults;
import pink.zak.giveawaybot.enums.ReturnCode;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.service.tuple.ImmutablePair;
import pink.zak.giveawaybot.storage.ScheduledGiveawayStorage;

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
        if (server.scheduledGiveaways().size() >= 10) {
            return ImmutablePair.of(null, ReturnCode.GIVEAWAY_LIMIT_FAILURE);
        }
        if (!giveawayChannel.getGuild().getSelfMember().hasPermission(giveawayChannel, this.defaults.getRequiredPermissions())) {
            return ImmutablePair.of(null, ReturnCode.PERMISSIONS_FAILURE);
        }
        if (!presetName.equalsIgnoreCase("default") && server.preset(presetName) == null) {
            return ImmutablePair.of(null, ReturnCode.NO_PRESET);
        }
        if (this.giveawayController.getGiveawayCountAt(server, startTime, endTime) >= 10) {
            return ImmutablePair.of(null, ReturnCode.FUTURE_GIVEAWAY_LIMIT_FAILURE);
        }
        ScheduledGiveaway giveaway = new ScheduledGiveaway(giveawayChannel.getIdLong(), server.id(), startTime, endTime, winnerAmount, presetName, giveawayItem);
        this.scheduledGiveawayCache.set(giveaway.uuid(), giveaway);
        server.scheduledGiveaways().add(giveaway.uuid());
        this.schedule(giveaway);
        return ImmutablePair.of(giveaway, ReturnCode.SUCCESS);
    }

    public void schedule(ScheduledGiveaway giveaway) {
        long waitTime = giveaway.millisToStart();
        this.scheduledGiveawayCache.addScheduledGiveaway(giveaway);
        if (waitTime <= 10000) {
            this.create(giveaway);
        } else {
            this.scheduler.schedule(() -> this.create(giveaway), waitTime, TimeUnit.MILLISECONDS);
        }
    }

    private void create(ScheduledGiveaway giveaway) {
        this.serverCache.get(giveaway.serverId()).thenAccept(server -> {
            Guild guild = this.shardManager.getGuildById(server.id());
            if (guild == null) {
                return;
            }
            TextChannel giveawayChannel = guild.getTextChannelById(giveaway.channelId());
            if (giveawayChannel == null) {
                return;
            }
            this.scheduledGiveawayCache.invalidate(giveaway.uuid(), false);
            server.scheduledGiveaways().remove(giveaway.uuid());
            this.giveawayController.createGiveaway(
                    server, giveaway.endTime() - giveaway.startTime(), giveaway.endTime(), giveaway.winnerAmount(), giveawayChannel, giveaway.presetName(), giveaway.giveawayItem()
            );
        });
    }

    public void deleteGiveaway(Server server, ScheduledGiveaway giveaway) {
        this.scheduledGiveawayCache.invalidate(giveaway.uuid(), false);
        this.scheduledGiveawayStorage.delete(giveaway.uuid());
        server.scheduledGiveaways().remove(giveaway.uuid());
    }

    private void load() {
        Set<ScheduledGiveaway> giveaways = this.scheduledGiveawayStorage.loadAll();
        Map<Long, Set<ScheduledGiveaway>> giveawaysToRemove = Maps.newHashMap();
        for (ScheduledGiveaway giveaway : giveaways) {
            if (System.currentTimeMillis() - giveaway.endTime() > 5000) {
                if (giveawaysToRemove.containsKey(giveaway.serverId())) {
                    giveawaysToRemove.get(giveaway.serverId()).add(giveaway);
                } else {
                    giveawaysToRemove.put(giveaway.serverId(), Sets.newHashSet(giveaway));
                }
                continue;
            }
            this.schedule(giveaway);
        }
        for (long serverId : giveawaysToRemove.keySet()) {
            this.serverCache.get(serverId).thenAccept(server -> {
                for (ScheduledGiveaway giveaway : giveawaysToRemove.get(serverId)) {
                    this.deleteGiveaway(server, giveaway);
                }
            });
        }
    }
}
