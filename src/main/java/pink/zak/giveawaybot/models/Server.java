package pink.zak.giveawaybot.models;

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
    private final Set<UUID> scheduledGiveaways;
    private final Set<Long> managerRoles;
    private final List<Long> bannedUsers;
    private Map<String, Preset> presets;
    private long premiumExpiry;
    private String language;

    public Server(GiveawayBot bot, long id, Set<Long> activeGiveaways, Set<UUID> scheduledGiveaways,
                  Map<String, Preset> presets, Set<Long> managerRoles, List<Long> bannedUsers, long premiumExpiry,
                  String language) {
        this.id = id;
        this.presets = presets;
        this.userStorage = new UserStorage(bot, this.id);
        this.userCache = new UserCache(bot, this.userStorage, this.id);
        this.activeGiveaways = activeGiveaways;
        this.scheduledGiveaways = scheduledGiveaways;
        this.managerRoles = managerRoles;
        this.bannedUsers = bannedUsers;
        this.premiumExpiry = premiumExpiry;
        this.language = language;


    }

    public Server(GiveawayBot bot, long id) {
        this(bot, id, Sets.newConcurrentHashSet(), Sets.newConcurrentHashSet(),
                new ConcurrentSkipListMap<>(), Sets.newHashSet(), Lists.newCopyOnWriteArrayList(), -1,
                "en-uk");
    }

    public long id() {
        return this.id;
    }

    public String stringId() {
        return String.valueOf(this.id);
    }

    public UserStorage userStorage() {
        return this.userStorage;
    }

    public UserCache userCache() {
        return this.userCache;
    }

    public Set<Long> activeGiveaways() {
        return this.activeGiveaways;
    }

    public Set<Long> activeGiveaways(User user) {
        Set<Long> enteredGiveaways = Sets.newHashSet();
        for (long giveawayId : this.activeGiveaways) {
            if (user.entries().containsKey(giveawayId) && user.hasEntries(giveawayId)) {
                enteredGiveaways.add(giveawayId);
            }
        }
        return enteredGiveaways;
    }

    public void addActiveGiveaway(CurrentGiveaway giveaway) {
        this.activeGiveaways.add(giveaway.messageId());
    }

    public Set<UUID> scheduledGiveaways() {
        return this.scheduledGiveaways;
    }

    public Preset preset(String name) {
        return this.presets.get(name.toLowerCase());
    }

    public Map<String, Preset> presets() {
        return this.presets;
    }

    public void setPresets(Map<String, Preset> presets) {
        this.presets = presets;
    }

    public void addPreset(Preset preset) {
        this.presets.put(preset.name().toLowerCase(), preset);
    }

    public Set<Long> managerRoles() {
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

    public List<Long> bannedUsers() {
        return this.bannedUsers;
    }

    public boolean isPremium() {
        return this.premiumExpiry > System.currentTimeMillis();
    }

    public long premiumExpiry() {
        return this.premiumExpiry;
    }

    public long timeToPremiumExpiry() {
        return this.premiumExpiry - System.currentTimeMillis();
    }

    public void setPremiumExpiry(long premiumExpiry) {
        this.premiumExpiry = premiumExpiry;
    }

    public String language() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
