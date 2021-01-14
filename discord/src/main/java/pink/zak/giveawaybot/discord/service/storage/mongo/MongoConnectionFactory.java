package pink.zak.giveawaybot.discord.service.storage.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import pink.zak.giveawaybot.discord.service.storage.settings.StorageSettings;

import java.util.Collections;

public class MongoConnectionFactory {
    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    public MongoConnectionFactory(StorageSettings storageSettings) {
        ServerAddress address = new ServerAddress(storageSettings.getHost(), Integer.parseInt(storageSettings.getPort()));
        MongoCredential credential = MongoCredential.createCredential(storageSettings.getUsername(), storageSettings.getAuthDatabase(), storageSettings.getPassword().toCharArray());

        this.mongoClient = MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(address)))
                .credential(credential)
                .build());
        this.mongoDatabase = this.mongoClient.getDatabase(storageSettings.getDatabase());
    }

    public MongoClient getClient() {
        return this.mongoClient;
    }

    public MongoDatabase getDatabase() {
        return this.mongoDatabase;
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        try {
            return this.mongoDatabase.getCollection(collectionName);
        } catch (IllegalArgumentException ex) {
            this.mongoDatabase.createCollection(collectionName);
            return this.mongoDatabase.getCollection(collectionName);
        }
    }

    public void close() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }
}
