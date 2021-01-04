package pink.zak.giveawaybot.api.controllers;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.stereotype.Component;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.api.exception.GuildNotFoundException;
import pink.zak.giveawaybot.api.exception.MemberNotFoundException;
import pink.zak.giveawaybot.api.model.server.PremiumTimeAdd;
import pink.zak.giveawaybot.cache.*;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.User;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.models.giveaway.ScheduledGiveaway;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class ServerControllerImpl implements ServerController {
    private final ServerCache serverCache = GiveawayBot.apiInstance.getServerCache();
    private final GiveawayCache giveawayCache = GiveawayBot.apiInstance.getGiveawayCache();
    private final ScheduledGiveawayCache scheduledGiveawayCache = GiveawayBot.apiInstance.getScheduledGiveawayCache();
    private final FinishedGiveawayCache finishedGiveawayCache = GiveawayBot.apiInstance.getFinishedGiveawayCache();
    private final GiveawayBot bot = GiveawayBot.apiInstance;

    @Override
    public Server getServer(long serverId) {
        return this.serverCache.getSync(serverId);
    }

    @Override
    public Set<CurrentGiveaway> getCurrentGiveaways(long serverId) {
        Server server = this.getServer(serverId);
        Set<CurrentGiveaway> currentGiveaways = Sets.newHashSet();
        for (long giveawayId : server.getActiveGiveaways()) {
            CurrentGiveaway giveaway = this.giveawayCache.getSync(giveawayId);
            if (giveaway != null) {
                currentGiveaways.add(giveaway);
            }
        }
        return currentGiveaways;
    }

    @Override
    public Set<ScheduledGiveaway> getScheduledGiveaways(long serverId) {
        Server server = this.getServer(serverId);
        Set<ScheduledGiveaway> scheduledGiveaways = Sets.newHashSet();
        for (UUID giveawayId : server.getScheduledGiveaways()) {
            ScheduledGiveaway giveaway = this.scheduledGiveawayCache.getSync(giveawayId);
            if (giveaway != null) {
                scheduledGiveaways.add(giveaway);
            }
        }
        return scheduledGiveaways;
    }

    @Override
    public List<FinishedGiveaway> getFinishedGiveaways(long serverId, boolean order) {
        Server server = this.getServer(serverId);
        return this.finishedGiveawayCache.getAll(server, order);
    }

    @Override
    public List<User> getBannedUsers(long serverId) {
        Server server = this.getServer(serverId);
        UserCache userCache = server.getUserCache();
        List<User> bannedUsers = Lists.newArrayList();
        for (long userId : server.getBannedUsers()) {
            bannedUsers.add(userCache.getSync(userId));
        }
        return bannedUsers;
    }

    @Override
    public User getUser(long serverId, long userId) {
        return this.serverCache.getSync(serverId).getUserCache().getSync(userId);
    }

    @Override
    public boolean isUserManager(long serverId, long userId) {
        Guild guild = this.bot.getShardManager().getGuildById(serverId);
        if (guild == null) {
            throw new GuildNotFoundException();
        }
        Member member = guild.getMemberById(userId);
        if (member == null) {
            member = guild.retrieveMemberById(userId).complete();
        }
        if (member == null) {
            throw new MemberNotFoundException();
        }
        return this.serverCache.getSync(serverId).canMemberManage(member);
    }

    @Override
    public long addPremiumTime(long serverId, PremiumTimeAdd payload) {
        System.out.println(payload);
        long premiumTime = payload.getTimeToAdd();
        return this.serverCache.getSync(serverId).addPremiumTime(premiumTime);
    }
}
