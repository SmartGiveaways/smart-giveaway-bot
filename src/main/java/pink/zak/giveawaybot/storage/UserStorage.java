package pink.zak.giveawaybot.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.models.User;
import pink.zak.giveawaybot.service.cache.options.CacheStorage;
import pink.zak.giveawaybot.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoSerializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoStorage;
import pink.zak.giveawaybot.service.types.MapCreator;

import java.util.EnumMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserStorage extends MongoStorage<Long, User> implements CacheStorage<Long, User> {
    private final long serverId;
    private final Gson gson;

    public UserStorage(GiveawayBot bot, long serverId) {
        super(bot, "users", "userId");
        this.serverId = serverId;

        this.gson = new GsonBuilder().registerTypeAdapter(new TypeToken<EnumMap<EntryType, AtomicInteger>>(){}.getType(), new MapCreator<>(EntryType.class)).create();
    }

    @Override
    public MongoSerializer<User> serializer() {
        return (user, document) -> {
            document.put("serverId", this.serverId);
            document.put("userId", user.id());
            document.put("banned", user.isBanned());
            document.put("shadowBanned", user.isShadowBanned());
            document.put("entries", this.gson.toJson(user.entries()));
            return document;
        };
    }

    @Override
    public MongoDeserializer<User> deserializer() {
        return document -> {
            long userId = document.getLong("userId");
            boolean banned = document.getBoolean("banned");
            boolean shadowBanned = document.getBoolean("shadowBanned");
            ConcurrentMap<Long, EnumMap<EntryType, AtomicInteger>> entries = this.gson.fromJson(document.getString("entries"), new TypeToken<ConcurrentHashMap<Long, EnumMap<EntryType, AtomicInteger>>>(){}.getType());
            return new User(userId, this.serverId, banned, shadowBanned, entries);
        };
    }

    @Override
    public User create(Long id) {
        return new User(id, this.serverId);
    }

    public long getServerId() {
        return this.serverId;
    }
}
