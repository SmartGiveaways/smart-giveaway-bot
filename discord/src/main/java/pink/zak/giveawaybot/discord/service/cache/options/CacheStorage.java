package pink.zak.giveawaybot.discord.service.cache.options;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CacheStorage<K, T> {

    CompletableFuture<T> load(K primaryKey, Map<String, Object> keyValues);

    CompletableFuture<T> load(K value);

    T create(K key);

    CompletableFuture<Void> save(T type);

    CompletableFuture<Void> save(Map<String, Object> keyValues, T type);
}
