package pink.zak.giveawaybot.models;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.UserCache;
import pink.zak.giveawaybot.storage.UserStorage;

import java.util.Map;
import java.util.Set;

public class Server {
    private final long id;
    private final UserStorage userStorage;
    private final UserCache userCache;
    private final Set<Long> activeGiveaways;
    private final Map<String, Preset> presets;
    private final Set<Long> managerRoles;

    public Server(GiveawayBot bot, long id, Set<Long> activeGiveaways, Map<String, Preset> presets, Set<Long> managerRoles) {
        this.id = id;
        this.presets = presets;
        this.userStorage = new UserStorage(bot, this.getId());
        this.userCache = new UserCache(bot, this.userStorage, this.id);
        this.activeGiveaways = activeGiveaways;
        this.managerRoles = managerRoles;
    }

    public Server(GiveawayBot bot, long id) {
        this(bot, id, Sets.newConcurrentHashSet(), Maps.newConcurrentMap(), Sets.newHashSet());
    }

    public long getId() {
        return this.id;
    }

    public String getStringId() {
        return String.valueOf(this.id);
    }

    public UserStorage getUserStorage() {
        return this.userStorage;
    }

    public UserCache getUserCache() {
        return this.userCache;
    }

    public Set<Long> getActiveGiveaways() {
        return this.activeGiveaways;
    }

    public void addActiveGiveaway(Giveaway giveaway) {
        this.activeGiveaways.add(giveaway.messageId());
    }

    public Preset getPreset(String name) {
        return this.presets.get(name.toLowerCase());
    }

    public Map<String, Preset> getPresets() {
        return this.presets;
    }

    public void addPreset(Preset preset) {
        this.presets.put(preset.name().toLowerCase(), preset);
    }

    public Set<Long> getManagerRoles() {
        return this.managerRoles;
    }

    public boolean canMemberManage(Member member) {
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            return true;
        }
        if (this.managerRoles.isEmpty()) {
            return false;
        }
        for (Role role : member.getRoles()) {
            if (this.managerRoles.contains(role.getIdLong())) {
                return true;
            }
        }
        return false;
    }

    public void addManagerRole(long managerRoleId) {
        this.managerRoles.add(managerRoleId);
    }

    public void removeManagerRole(long managerRoleId) {
        this.managerRoles.remove(managerRoleId);
    }
}
