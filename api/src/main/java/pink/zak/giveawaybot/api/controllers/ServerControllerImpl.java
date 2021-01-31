package pink.zak.giveawaybot.api.controllers;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import pink.zak.giveawaybot.api.model.server.PremiumTimeAdd;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.discord.data.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.data.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.discord.data.cache.ServerCache;
import pink.zak.giveawaybot.discord.data.cache.UserCache;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.data.models.User;
import pink.zak.giveawaybot.discord.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.data.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.discord.data.models.giveaway.finished.FullFinishedGiveaway;

import java.util.List;
import java.util.UUID;

@Component
public class ServerControllerImpl implements ServerController {
    private final ServerCache serverCache = GiveawayBot.apiInstance.getServerCache();
    private final GiveawayCache giveawayCache = GiveawayBot.apiInstance.getGiveawayCache();
    private final ScheduledGiveawayCache scheduledGiveawayCache = GiveawayBot.apiInstance.getScheduledGiveawayCache();
    private final FinishedGiveawayCache finishedGiveawayCache = GiveawayBot.apiInstance.getFinishedGiveawayCache();

    @Override
    public Server getServer(long serverId) {
        return this.serverCache.get(serverId);
    }

    @Override
    public List<CurrentGiveaway> getCurrentGiveaways(long serverId) {
        Server server = this.getServer(serverId);
        List<CurrentGiveaway> currentGiveaways = Lists.newArrayList();
        for (long giveawayId : server.getActiveGiveaways()) {
            CurrentGiveaway giveaway = this.giveawayCache.get(giveawayId);
            if (giveaway != null) {
                currentGiveaways.add(giveaway);
            }
        }
        return currentGiveaways;
    }

    @Override
    public List<ScheduledGiveaway> getScheduledGiveaways(long serverId) {
        Server server = this.getServer(serverId);
        List<ScheduledGiveaway> scheduledGiveaways = Lists.newArrayList();
        for (UUID giveawayId : server.getScheduledGiveaways()) {
            ScheduledGiveaway giveaway = this.scheduledGiveawayCache.get(giveawayId);
            if (giveaway != null) {
                scheduledGiveaways.add(giveaway);
            }
        }
        return scheduledGiveaways;
    }

    @Override
    public List<FullFinishedGiveaway> getFinishedGiveaways(long serverId) {
        Server server = this.getServer(serverId);
        return this.finishedGiveawayCache.getAll(server);
    }

    @Override
    public List<User> getBannedUsers(long serverId) {
        Server server = this.getServer(serverId);
        UserCache userCache = server.getUserCache();
        List<User> bannedUsers = Lists.newArrayList();
        for (long userId : server.getBannedUsers()) {
            bannedUsers.add(userCache.get(userId));
        }
        return bannedUsers;
    }

    @Override
    public long addPremiumTime(long serverId, PremiumTimeAdd payload) {
        System.out.println(payload);
        long premiumTime = payload.getTimeToAdd();
        return this.serverCache.get(serverId).addPremiumTime(premiumTime);
    }
}
