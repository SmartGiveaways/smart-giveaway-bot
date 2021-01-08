package pink.zak.giveawaybot.discord.service.cache.options;

public interface CacheSaver<K, V> {

    void save(K key, V value);
}
