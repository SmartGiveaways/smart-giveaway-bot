package pink.zak.giveawaybot.service.cache.options;

import java.util.Map;

public interface CacheStorage<K, T> {

    T load(K primaryKey, Map<String, Object> keyValues);

    T load(K value);

    T create(K key);

    void save(T type);

    void save(Map<String, Object> keyValues, T type);
}
