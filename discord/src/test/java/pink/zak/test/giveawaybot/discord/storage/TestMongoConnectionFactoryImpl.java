package pink.zak.test.giveawaybot.discord.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pink.zak.giveawaybot.discord.service.storage.mongo.factory.MongoConnectionFactory;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class TestMongoConnectionFactoryImpl implements MongoConnectionFactory {
    @Mock
    private MongoClient client;
    @Mock
    private MongoDatabase database;
    @Mock
    private MongoCollection<Document> finishedGiveawayConnection;
    @Mock
    private MongoCollection<Document> giveawayCollection;
    @Mock
    private MongoCollection<Document> scheduledGiveawayCollection;
    @Mock
    private MongoCollection<Document> serverCollection;
    @Mock
    private MongoCollection<Document> userCollection;

    public TestMongoConnectionFactoryImpl() {
        this.initMocks();
    }

    public void initMocks() {
        MockitoAnnotations.openMocks(this);
        when(this.client.getDatabase(anyString())).thenReturn(this.database);
        when(this.database.getCollection("users")).thenReturn(this.userCollection);
        when(this.database.getCollection("server-settings")).thenReturn(this.serverCollection);
        when(this.database.getCollection("scheduled-giveaways")).thenReturn(this.scheduledGiveawayCollection);
        when(this.database.getCollection("giveaways")).thenReturn(this.giveawayCollection);
        when(this.database.getCollection("finished-giveaways")).thenReturn(this.finishedGiveawayConnection);
    }

    @Override
    public MongoClient getClient() {
        return this.client;
    }

    @Override
    public MongoDatabase getDatabase() {
        return this.database;
    }

    @Override
    public MongoCollection<Document> getCollection(String collectionName) {
        return this.database.getCollection(collectionName);
    }
}
