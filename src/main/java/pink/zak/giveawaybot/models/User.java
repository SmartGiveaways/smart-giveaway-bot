package pink.zak.giveawaybot.models;

import com.google.common.collect.Maps;
import pink.zak.giveawaybot.enums.EntryType;

import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public record User(long id, long serverId, Map<UUID, EnumMap<EntryType, Integer>> entries) {

    public User(long id, long serverId) {
        this(id, serverId, Maps.newConcurrentMap());
    }

    public BigInteger getEntries(UUID uuid) {
        BigInteger total = BigInteger.ZERO;
        for (Map.Entry<EntryType, Integer> entry : this.entries.get(uuid).entrySet()) {
            total = total.add(BigInteger.valueOf(entry.getValue()));
        }
        return total;
    }

    public boolean hasEntries(UUID uuid) {
        return this.getEntries(uuid).compareTo(BigInteger.ZERO) > 0;
    }
}
