package pink.zak.giveawaybot.models;

import pink.zak.giveawaybot.enums.EntryType;

import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private final long id;
    private final long serverId;
    private final ConcurrentHashMap<Long, EnumMap<EntryType, AtomicInteger>> entries;
    private boolean banned;
    private boolean shadowBanned;

    public User(long id, long serverId, boolean banned, boolean shadowBanned, ConcurrentHashMap<Long, EnumMap<EntryType, AtomicInteger>> entries) {
        this.id = id;
        this.serverId = serverId;
        this.banned = banned;
        this.shadowBanned = shadowBanned;
        this.entries = entries;
    }

    public User(long id, long serverId) {
        this(id, serverId, false, false, new ConcurrentHashMap<>());
    }

    public BigInteger entries(long id) {
        BigInteger total = BigInteger.ZERO;
        if (!this.entries.containsKey(id)) {
            return total;
        }
        for (Map.Entry<EntryType, AtomicInteger> entry : this.entries.get(id).entrySet()) { // TODO error here
            total = total.add(BigInteger.valueOf(entry.getValue().get()));
        }
        return total;
    }

    public boolean hasEntries(long id) {
        return this.entries(id).compareTo(BigInteger.ZERO) > 0;
    }

    public long id() {
        return this.id;
    }

    public long serverId() {
        return this.serverId;
    }

    public void ban() {
        this.banned = true;
    }

    public void unBan() {
        this.banned = false;
    }

    public boolean isBanned() {
        return this.banned;
    }

    public void shadowBan() {
        this.shadowBanned = true;
    }

    public void unShadowBan() {
        this.shadowBanned = false;
    }

    public boolean isShadowBanned() {
        return this.shadowBanned;
    }

    public ConcurrentHashMap<Long, EnumMap<EntryType, AtomicInteger>> entries() {
        return this.entries;
    }
}
