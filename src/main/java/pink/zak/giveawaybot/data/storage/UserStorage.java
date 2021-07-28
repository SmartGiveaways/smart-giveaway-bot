package pink.zak.giveawaybot.data.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import pink.zak.giveawaybot.data.models.User;
import pink.zak.giveawaybot.pipelines.entries.EntryType;
import pink.zak.giveawaybot.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoSerializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoStorage;
import pink.zak.giveawaybot.service.storage.mongo.factory.MongoConnectionFactory;
import pink.zak.giveawaybot.threads.ThreadManager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserStorage extends MongoStorage<Long, User> {
    private final long serverId;

    public UserStorage(ThreadManager threadManager, MongoConnectionFactory connectionFactory, long serverId) {
        super(threadManager, connectionFactory, "users", "userId");
        this.serverId = serverId;
    }

    @Override
    public MongoSerializer<User> serializer() {
        return (user, document) -> {
            document.put("serverId", this.serverId);
            document.put("userId", user.getId());
            document.put("banned", user.isBanned());
            document.put("shadowBanned", user.isShadowBanned());
            Set<BasicDBObject> entries = this.createEntries(user);
            if (!entries.isEmpty())
                document.put("entries", entries);
            return document;
        };
    }

    @Override
    public MongoDeserializer<User> deserializer() {
        return document -> {
            long userId = document.getLong("userId");
            boolean banned = document.getBoolean("banned");
            boolean shadowBanned = document.getBoolean("shadowBanned");
            ConcurrentMap<Long, Map<EntryType, AtomicInteger>> entries = this.createEntries(document);
            return new User(userId, this.serverId, banned, shadowBanned, entries);
        };
    }

    private Set<BasicDBObject> createEntries(User user) {
        Set<BasicDBObject> documents = Sets.newHashSet();
        Map<Long, Map<EntryType, AtomicInteger>> entries = user.getEntries();
        if (user.getEntries().isEmpty())
            return documents;

        for (Map.Entry<Long, Map<EntryType, AtomicInteger>> giveawayEntry : entries.entrySet()) {
            BasicDBObject dbObject = new BasicDBObject();
            dbObject.put("id", giveawayEntry.getKey());
            Set<BasicDBObject> entryObjects = Sets.newHashSet();
            for (Map.Entry<EntryType, AtomicInteger> entryTypeEntry : giveawayEntry.getValue().entrySet()) {
                BasicDBObject entryTypeObject = new BasicDBObject();
                entryTypeObject.put("type", entryTypeEntry.getKey().toString());
                entryTypeObject.put("value", entryTypeEntry.getValue().intValue());
                entryObjects.add(entryTypeObject);
            }
            dbObject.put("entries", entryObjects);
            documents.add(dbObject);
        }
        return documents;
    }

    private ConcurrentHashMap<Long, Map<EntryType, AtomicInteger>> createEntries(Document userDocument) {
        ConcurrentHashMap<Long, Map<EntryType, AtomicInteger>> map = new ConcurrentHashMap<>();
        if (!userDocument.containsKey("entries"))
            return map;

        List<Document> giveawayDocuments = userDocument.getList("entries", Document.class);

        for (Document giveawayDocument : giveawayDocuments) {
            long giveawayId = giveawayDocument.getLong("id");
            List<Document> entryDocuments = giveawayDocument.getList("entries", Document.class);

            Map<EntryType, AtomicInteger> entryMap = Maps.newEnumMap(EntryType.class);
            for (Document entryDocument : entryDocuments) {
                EntryType entryType = EntryType.valueOf(entryDocument.getString("type"));
                AtomicInteger value = new AtomicInteger(entryDocument.getInteger("value"));

                entryMap.put(entryType, value);
            }

            map.put(giveawayId, entryMap);
        }
        return map;
    }

    @Override
    public User create(Long id) {
        User user = new User(id, this.serverId);
        this.save(user);
        return user;
    }

    public long getServerId() {
        return this.serverId;
    }
}
