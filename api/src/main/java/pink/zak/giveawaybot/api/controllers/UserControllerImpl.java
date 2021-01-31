package pink.zak.giveawaybot.api.controllers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.stereotype.Component;
import pink.zak.giveawaybot.api.exception.GuildNotFoundException;
import pink.zak.giveawaybot.api.exception.MemberNotFoundException;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.cache.ServerCache;
import pink.zak.giveawaybot.discord.data.models.User;

@Component
public class UserControllerImpl implements UserController {
    private final ShardManager shardManager = GiveawayBot.apiInstance.getShardManager();
    private final ServerCache serverCache = GiveawayBot.apiInstance.getServerCache();

    @Override
    public User getUser(long serverId, long userId) {
        return this.serverCache.get(serverId).getUserCache().get(userId);
    }

    @Override
    public boolean isUserManager(long serverId, long userId) {
        Guild guild = this.shardManager.getGuildById(serverId);
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
        return this.serverCache.get(serverId).canMemberManage(member);
    }
}
