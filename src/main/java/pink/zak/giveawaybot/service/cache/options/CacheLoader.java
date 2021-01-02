package pink.zak.giveawaybot.service.cache.options;

public interface CacheLoader<K, V> {

    V load(K key);
}
