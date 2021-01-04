package pink.zak.giveawaybot.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.UserCache;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.storage.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

public class Server {
    private final long id;
    private final UserStorage userStorage;
    private final UserCache userCache;
    private final Set<Long> activeGiveaways;
    private final Set<Long> finishedGiveaways;
    private final Set<UUID> scheduledGiveaways;
    private final Set<Long> managerRoles;
    private final List<Long> bannedUsers;
    private Map<String, Preset> presets;
    private long premiumExpiry;
    private String language;

    public Server(GiveawayBot bot, long id, Set<Long> activeGiveaways, Set<Long> finishedGiveaways,
                  Set<UUID> scheduledGiveaways, Map<String, Preset> presets, Set<Long> managerRoles, List<Long> bannedUsers,
                  long premiumExpiry, String language) {
        this.id = id;
        this.presets = presets;
        this.userStorage = new UserStorage(bot, this.id);
        this.userCache = new UserCache(bot, this.userStorage, this.id);
        this.activeGiveaways = activeGiveaways;
        this.finishedGiveaways = finishedGiveaways;
        this.scheduledGiveaways = scheduledGiveaways;
        this.managerRoles = managerRoles;
        this.bannedUsers = bannedUsers;
        this.premiumExpiry = premiumExpiry;
        this.language = language;
    }

    public Server(GiveawayBot bot, long id) {
        this(bot, id, Sets.newConcurrentHashSet(), Sets.newHashSet(), Sets.newConcurrentHashSet(),
                new ConcurrentSkipListMap<>(), Sets.newHashSet(), Lists.newCopyOnWriteArrayList(), -1,
                "en-uk");
    }

    public long getId() {
        return this.id;
    }

    @JsonIgnore
    public String getStringId() {
        return String.valueOf(this.id);
    }

    @JsonIgnore
    public UserStorage getUserStorage() {
        return this.userStorage;
    }

    @JsonIgnore
    public UserCache getUserCache() {
        return this.userCache;
    }

    public Set<Long> getActiveGiveaways() {
        return this.activeGiveaways;
    }

    public Set<Long> getActiveGiveaways(User user) {
        Set<Long> enteredGiveaways = Sets.newHashSet();
        for (long giveawayId : this.activeGiveaways) {
            if (user.getEntries().containsKey(giveawayId) && user.hasEntries(giveawayId)) {
                enteredGiveaways.add(giveawayId);
            }
        }
        return enteredGiveaways;
    }

    public void addActiveGiveaway(CurrentGiveaway giveaway) {
        this.activeGiveaways.add(giveaway.getMessageId());
    }

    public Set<Long> getFinishedGiveaways() {
        return this.finishedGiveaways;
    }

    public Set<UUID> getScheduledGiveaways() {
        return this.scheduledGiveaways;
    }

    public Preset getPreset(String name) {
        return this.presets.get(name.toLowerCase());
    }

    public Map<String, Preset> getPresets() {
        return this.presets;
    }

    public void setPresets(Map<String, Preset> presets) {
        this.presets = presets;
    }

    public void addPreset(Preset preset) {
        this.presets.put(preset.getName().toLowerCase(), preset);
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

    public List<Long> getBannedUsers() {
        return this.bannedUsers;
    }

    public boolean isPremium() {
        return this.premiumExpiry > System.currentTimeMillis();
    }

    public long getPremiumExpiry() {
        return this.premiumExpiry;
    }

    public long getTimeToPremiumExpiry() {
        return this.premiumExpiry - System.currentTimeMillis();
    }

    public void setPremiumExpiry(long premiumExpiry) {
        this.premiumExpiry = premiumExpiry;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
