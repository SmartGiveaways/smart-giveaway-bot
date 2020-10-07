package pink.zak.giveawaybot.models;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.UserCache;
import pink.zak.giveawaybot.storage.UserStorage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Server {
    private final long id;
    private final UserStorage userStorage;
    private final UserCache userCache;
    private final Map<Long, UUID> activeGiveaways;
    private final Map<String, Preset> presets;
    private long managerRoleId;

    public Server(GiveawayBot bot, long id, Map<Long, UUID> activeGiveaways, Map<String, Preset> presets, long managerRoleId) {
        this.id = id;
        this.presets = presets;
        this.userStorage = new UserStorage(bot, this.getId());
        this.userCache = new UserCache(bot, this.userStorage, TimeUnit.MINUTES, 10, this.id);
        this.activeGiveaways = activeGiveaways;
        this.managerRoleId = managerRoleId;
    }

    public Server(GiveawayBot bot, long id) {
        this(bot, id, Maps.newConcurrentMap(), Maps.newConcurrentMap(), 0);
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

    public Map<Long, UUID> getActiveGiveaways() {
        return this.activeGiveaways;
    }

    public void addActiveGiveaway(Giveaway giveaway) {
        this.activeGiveaways.put(giveaway.messageId(), giveaway.uuid());
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

    public long getManagerRoleId() {
        return this.managerRoleId;
    }

    public boolean canMemberManage(Member member) {
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            return true;
        }
        if (this.managerRoleId == 0) {
            return false;
        }
        for (Role role : member.getRoles()) {
            if (this.managerRoleId == role.getIdLong()) {
                return true;
            }
        }
        return false;
    }

    public void setManagerRoleId(long managerRoleId) {
        this.managerRoleId = managerRoleId;
    }
}
