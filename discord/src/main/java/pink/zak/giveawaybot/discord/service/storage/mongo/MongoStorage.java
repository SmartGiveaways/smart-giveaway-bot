package pink.zak.giveawaybot.discord.service.storage.mongo;

import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.threads.ThreadFunction;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public abstract class MongoStorage<K, T> {
    protected final MongoCollection<Document> collection;
    private final String idKey;
    private ExecutorService executorService;

    public MongoStorage(GiveawayBot bot, String collectionName, String idKey) {
        this.collection = bot.getMongoConnectionFactory().getCollection(collectionName);
        this.executorService = bot.getAsyncExecutor(ThreadFunction.STORAGE);
        this.idKey = idKey;
    }

    public abstract MongoSerializer<T> serializer();

    public abstract MongoDeserializer<T> deserializer();

    public abstract T create(K id);

    public CompletableFuture<T> load(K primaryKey, Map<String, Object> keyValues) {
        return CompletableFuture.supplyAsync(() -> {
            Document foundDocument = this.find(new BasicDBObject(keyValues));
            if (foundDocument == null) {
                return this.create(primaryKey);
            }
            return this.deserializer().apply(foundDocument);
        }, this.executorService);
    }

    public CompletableFuture<T> load(String key, Object value) {
        return CompletableFuture.supplyAsync(() -> {
            Document foundDocument = this.find(Filters.eq(key, value));
            if (foundDocument == null) {
                return null;
            }
            return this.deserializer().apply(foundDocument);
        }, this.executorService);
    }

    public CompletableFuture<T> load(K key) {
        return CompletableFuture.supplyAsync(() -> {
            Document foundDocument = this.find(Filters.eq(this.idKey, key));
            if (foundDocument == null) {
                return this.create(key);
            }
            return this.deserializer().apply(foundDocument);
        });
    }

    public CompletableFuture<Set<T>> loadAll() {
        return CompletableFuture.supplyAsync(() -> {
            Set<T> loaded = Sets.newHashSet();
            for (Document document : this.collection.find()) {
                loaded.add(this.deserializer().apply(document));
            }
            return loaded;
        }, this.executorService);
    }

    private void save(Bson filter, Document document) {
        if (this.find(filter) == null) {
            this.collection.insertOne(document);
        } else {
            this.collection.findOneAndReplace(filter, document);
        }
    }

    public CompletableFuture<Void> save(T type) {
        return CompletableFuture.runAsync(() -> {
            Document document = this.serializer().apply(type, new Document());
            this.save(Filters.eq(this.idKey, document.get(this.idKey)), document);
        }, this.executorService);
    }

    public CompletableFuture<Void> save(Map<String, Object> keyValues, T type) {
        return CompletableFuture.runAsync(() -> {
            Document document = this.serializer().apply(type, new Document());
            this.save(new BasicDBObject(keyValues), document);
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

    private Document find(Bson filter) {
        return this.collection.find(filter).first();
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
