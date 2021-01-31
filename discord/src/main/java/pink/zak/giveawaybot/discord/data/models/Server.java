package pink.zak.giveawaybot.discord.data.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.cache.UserCache;
import pink.zak.giveawaybot.discord.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.data.storage.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

public class Server {
    private final long id;
    private final UserStorage userStorage;
    private final UserCache userCache;
    private final List<Long> activeGiveaways;
    private final List<UUID> scheduledGiveaways;
    private final List<Long> finishedGiveaways;
    private final List<Long> bannedUsers;
    private final Set<Long> managerRoles;
    private Map<String, Preset> presets;
    private long premiumExpiry;
    private String language;

    public Server(GiveawayBot bot, long id, List<Long> activeGiveaways, List<Long> finishedGiveaways,
                  List<UUID> scheduledGiveaways, List<Long> bannedUsers, Set<Long> managerRoles, Map<String, Preset> presets,
                  long premiumExpiry, String language) {
        this.id = id;
        this.presets = presets;
        this.userStorage = new UserStorage(bot.getThreadManager(), bot.getMongoConnectionFactory(), this.id);
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
        this(bot, id, Lists.newCopyOnWriteArrayList(), Lists.newCopyOnWriteArrayList(), Lists.newCopyOnWriteArrayList(),
                Lists.newCopyOnWriteArrayList(), Sets.newHashSet(), new ConcurrentSkipListMap<>(), -1,
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

    public List<Long> getActiveGiveaways() {
        return this.activeGiveaways;
    }

    public List<Long> getActiveGiveaways(User user) {
        List<Long> enteredGiveaways = Lists.newArrayList();
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

    public List<Long> getFinishedGiveaways() {
        return this.finishedGiveaways;
    }

    public List<UUID> getScheduledGiveaways() {
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

    public long addPremiumTime(long toAdd) {
        this.premiumExpiry = (this.isPremium() ? this.premiumExpiry : System.currentTimeMillis()) + toAdd;
        this.setPremiumExpiry(premiumExpiry);
        return this.premiumExpiry;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
