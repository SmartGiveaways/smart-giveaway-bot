package pink.zak.giveawaybot.storage;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.models.User;
import pink.zak.giveawaybot.service.storage.settings.StorageType;
import pink.zak.giveawaybot.service.storage.storage.Storage;
import pink.zak.giveawaybot.service.storage.storage.serialization.Deserializer;
import pink.zak.giveawaybot.service.storage.storage.serialization.Serializer;
import pink.zak.giveawaybot.service.types.MapCreator;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserStorage extends Storage<User> {
    private final long serverId;

    public UserStorage(GiveawayBot bot, long serverId) {
        super(bot, factory -> factory.create(StorageType.MONGODB, "users"));
        this.serverId = serverId;

        this.gsonBuilder.registerTypeAdapter(new TypeToken<EnumMap<EntryType, AtomicInteger>>(){}.getType(), new MapCreator<>(EntryType.class));
        this.saveChanges();
    }

    @Override
    public Serializer<User> serializer() {
        return (user, json, gson) -> {
            json.addProperty("userId", String.valueOf(user.id()));
            json.addProperty("serverId", String.valueOf(this.serverId));
            json.addProperty("entries", gson.toJson(user.entries()));
            return json;
        };
    }

    @Override
    public Deserializer<User> deserializer() {
        return (json, gson) -> {
            long id = json.get("userId").getAsLong();
            ConcurrentHashMap<UUID, EnumMap<EntryType, AtomicInteger>> entries = gson.fromJson(json.get("entries").getAsString(), new TypeToken<ConcurrentHashMap<UUID, EnumMap<EntryType, AtomicInteger>>>(){}.getType());
            return new User(id, this.serverId, entries);
        };
    }

    @Override
    public User create(String id) {
        return new User(Long.parseLong(id), this.serverId);
    }

    @Override
    public User load(Map<String, String> valuePairs) {
        JsonObject json = super.backend.load(valuePairs);
        return json == null ? this.create(valuePairs.values().toArray(new String[]{})[1]) : this.deserializer().apply(json, super.gson);
    }

    public long getServerId() {
        return this.serverId;
    }
}
