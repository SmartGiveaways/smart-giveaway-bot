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
        super(bot, "users");
        this.serverId = serverId;

        this.gson = new GsonBuilder().registerTypeAdapter(new TypeToken<EnumMap<EntryType, AtomicInteger>>(){}.getType(), new MapCreator<>(EntryType.class)).create();
    }

    /*@Override
    public Serializer<User> serializer() {
        return (user, json, gson) -> {
            json.addProperty("userId", String.valueOf(user.id()));
            json.addProperty("serverId", String.valueOf(this.serverId));
            json.addProperty("banned", String.valueOf(user.isBanned()));
            json.addProperty("shadowBanned", String.valueOf(user.isShadowBanned()));
            json.addProperty("entries", gson.toJson(user.entries()));
            return json;
        };
    }*/

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

    /*@Override
    public Deserializer<User> deserializer() {
        return (json, gson) -> {
            long id = json.get("userId").getAsLong();
            boolean banned = json.get("banned").getAsBoolean();
            boolean shadowBanned = json.get("shadowBanned").getAsBoolean();
            ConcurrentMap<Long, EnumMap<EntryType, AtomicInteger>> entries = gson.fromJson(json.get("entries").getAsString(), new TypeToken<ConcurrentHashMap<Long, EnumMap<EntryType, AtomicInteger>>>(){}.getType());
            return new User(id, this.serverId, banned, shadowBanned, entries);
        };
    }*/

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

    /*@Override
    public User create(String id) {
        return new User(Long.parseLong(id), this.serverId);
    }*/

    /*@Override
    public User load(Map<String, String> valuePairs) {
        JsonObject json = super.backend.load(valuePairs);
        return json == null ? this.create(valuePairs.values().toArray(new String[]{})[1]) : this.deserializer().apply(json, super.gson);
    }*/

    public long getServerId() {
        return this.serverId;
    }
}
