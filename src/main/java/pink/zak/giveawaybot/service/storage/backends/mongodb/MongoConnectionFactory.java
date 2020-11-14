package pink.zak.giveawaybot.service.storage.backends.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import pink.zak.giveawaybot.service.storage.settings.StorageSettings;

public class MongoConnectionFactory {
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;

    public MongoConnectionFactory(StorageSettings storageSettings) {
        if (mongoClient != null && mongoDatabase != null) {
            return;
        }
        ServerAddress address = new ServerAddress(storageSettings.getHost(), Integer.parseInt(storageSettings.getPort()));
        if (storageSettings.getPassword().isEmpty()) {
            mongoClient = new MongoClient(address);
        } else {
            MongoCredential credential = MongoCredential.createCredential(storageSettings.getUsername(), storageSettings.getAuthDatabase(), storageSettings.getPassword().toCharArray());
            mongoClient = new MongoClient(address, credential, new MongoClientOptions.Builder().build());
        }
        mongoDatabase = mongoClient.getDatabase(storageSettings.getDatabase());
    }

    public static MongoDatabase getDatabase() {
        return mongoDatabase;
    }

    public static MongoCollection<Document> getCollection(String collectionName) {
        try {
            return mongoDatabase.getCollection(collectionName);
        } catch (IllegalArgumentException ex) {
            mongoDatabase.createCollection(collectionName);
            return mongoDatabase.getCollection(collectionName);
        }
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
