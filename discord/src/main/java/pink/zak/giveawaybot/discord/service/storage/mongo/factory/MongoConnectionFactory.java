package pink.zak.giveawaybot.discord.service.storage.mongo.factory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public interface MongoConnectionFactory {

    MongoClient getClient();

    MongoDatabase getDatabase();

    MongoCollection<Document> getCollection(String collectionName);

    default void close() {
        if (this.getClient() != null) {
            this.getClient().close();
        }
    }
}
