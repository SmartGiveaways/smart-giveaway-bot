package pink.zak.giveawaybot.models;

import com.google.common.collect.Maps;
import pink.zak.giveawaybot.enums.EntryType;

import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public record User(long id, long serverId, Map<UUID, EnumMap<EntryType, AtomicInteger>> entries) {

    public User(long id, long serverId) {
        this(id, serverId, Maps.newConcurrentMap());
    }

    public BigInteger getEntries(UUID uuid) {
        BigInteger total = BigInteger.ZERO;
        if (!this.entries.containsKey(uuid)) {
            return total;
        }
        for (Map.Entry<EntryType, AtomicInteger> entry : this.entries.get(uuid).entrySet()) { // TODO error here
            total = total.add(BigInteger.valueOf(entry.getValue().get()));
        }
        return total;
    }

    public boolean hasEntries(UUID uuid) {
        return this.getEntries(uuid).compareTo(BigInteger.ZERO) > 0;
    }
}
