package pink.zak.giveawaybot.service.cache.options;

public interface CacheExpiryListener<K, V> {

    void onExpiry(K key, V value);
}
