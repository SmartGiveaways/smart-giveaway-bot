package pink.zak.giveawaybot.service.cache.options;

public interface CacheSaver<K, V> {

    void save(K key, V value);
}
