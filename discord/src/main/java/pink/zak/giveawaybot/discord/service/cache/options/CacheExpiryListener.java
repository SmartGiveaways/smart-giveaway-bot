package pink.zak.giveawaybot.discord.service.cache.options;

public interface CacheExpiryListener<K, V> {

    void onExpiry(K key, V value);
}
