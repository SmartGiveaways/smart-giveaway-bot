package pink.zak.giveawaybot.service.storage.mongo;

import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.mongojack.JacksonMongoCollection;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.threads.ThreadFunction;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public abstract class MongoJacksonStorage<K, T> {
    protected final JacksonMongoCollection<T> collection;
    private ExecutorService executorService;
    private final String idKey;

    protected MongoJacksonStorage(GiveawayBot bot, String collectionName, String idKey, Class<T> clazz) {
        this.collection = JacksonMongoCollection.builder().build(
                bot.getMongoConnectionFactory().getClient(), bot.getStorageSettings().getDatabase(), collectionName, clazz, UuidRepresentation.STANDARD);
        this.executorService = bot.getAsyncExecutor(ThreadFunction.STORAGE);
        this.idKey = idKey;
    }

    public abstract MongoSerializer<T> serializer();

    public abstract MongoDeserializer<T> deserializer();

    public abstract T create(K id);

    public CompletableFuture<T> load(K primaryKey, Map<String, Object> keyValues) {
        return CompletableFuture.supplyAsync(() -> {
            T foundDocument = this.find(new BasicDBObject(keyValues));
            return foundDocument != null ? foundDocument : this.create(primaryKey);
        }, this.executorService);
    }

    public CompletableFuture<T> load(K key) {
        return CompletableFuture.supplyAsync(() -> {
            T foundDocument = this.find(Filters.eq(this.idKey, key));
            return foundDocument != null ? foundDocument : this.create(key);
        });
    }

    public CompletableFuture<Set<T>> loadAll() {
        return CompletableFuture.supplyAsync(() -> Sets.newHashSet(this.collection.find()), this.executorService);
    }

    public CompletableFuture<Void> save(T type) {
        return CompletableFuture.runAsync(() -> this.collection.save(type), this.executorService);
    }

    public CompletableFuture<Void> save(Map<String, Object> keyValues, T type) {
        return CompletableFuture.runAsync(() -> {
            Bson filter = new BasicDBObject(keyValues);
            if (this.find(filter) == null) {
                this.collection.insertOne(type);
            } else {
                this.collection.findOneAndReplace(filter, type);
            }
        }, this.executorService);
    }

    public void delete(Bson filter) {
        this.collection.deleteOne(filter);
    }

    public void delete(K key) {
        this.delete(Filters.eq(this.idKey, key));
    }

    public void delete(Map<String, Object> keyValues) {
        this.delete((Bson) new BasicDBObject(keyValues));
    }

    private T find(Bson filter) {
        return this.collection.findOne(filter);
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
