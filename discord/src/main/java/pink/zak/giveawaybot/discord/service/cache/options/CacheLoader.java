package pink.zak.giveawaybot.discord.service.cache.options;

public interface CacheLoader<K, V> {

    V load(K key);
}
