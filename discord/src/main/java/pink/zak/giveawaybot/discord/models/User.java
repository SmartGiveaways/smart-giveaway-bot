package pink.zak.giveawaybot.discord.models;

import pink.zak.giveawaybot.discord.enums.EntryType;

import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private final long id;
    private final long serverId;
    private final ConcurrentMap<Long, EnumMap<EntryType, AtomicInteger>> entries;
    private boolean banned;
    private boolean shadowBanned;

    public User(long id, long serverId, boolean banned, boolean shadowBanned, ConcurrentMap<Long, EnumMap<EntryType, AtomicInteger>> entries) {
        this.id = id;
        this.serverId = serverId;
        this.banned = banned;
        this.shadowBanned = shadowBanned;
        this.entries = entries;
    }

    public User(long id, long serverId) {
        this(id, serverId, false, false, new ConcurrentHashMap<>());
    }

    public BigInteger getEntries(long id) {
        BigInteger total = BigInteger.ZERO;
        if (!this.entries.containsKey(id)) {
            return total;
        }
        for (Map.Entry<EntryType, AtomicInteger> entry : this.entries.get(id).entrySet()) {
            total = total.add(BigInteger.valueOf(entry.getValue().get()));
        }
        return total;
    }

    public boolean hasEntries(long id) {
        return this.getEntries(id).compareTo(BigInteger.ZERO) > 0;
    }

    public long getId() {
        return this.id;
    }

    public long getServerId() {
        return this.serverId;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public boolean isBanned() {
        return this.banned;
    }

    public void setShadowBanned(boolean shadowBanned) {
        this.shadowBanned = shadowBanned;
    }

    public boolean isShadowBanned() {
        return this.shadowBanned;
    }

    public ConcurrentMap<Long, EnumMap<EntryType, AtomicInteger>> getEntries() {
        return this.entries;
    }
}
