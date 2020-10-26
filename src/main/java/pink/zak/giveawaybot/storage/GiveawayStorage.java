package pink.zak.giveawaybot.storage;

import com.google.common.collect.Sets;
import com.google.gson.reflect.TypeToken;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.service.cache.options.CacheLoader;
import pink.zak.giveawaybot.service.cache.options.CacheSaver;
import pink.zak.giveawaybot.service.storage.settings.StorageType;
import pink.zak.giveawaybot.service.storage.storage.Storage;
import pink.zak.giveawaybot.service.storage.storage.serialization.Deserializer;
import pink.zak.giveawaybot.service.storage.storage.serialization.Serializer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GiveawayStorage extends Storage<CurrentGiveaway> implements CacheLoader<UUID, CurrentGiveaway>, CacheSaver<UUID, CurrentGiveaway> {

    public GiveawayStorage(GiveawayBot bot) {
        super(bot, factory -> factory.create(StorageType.MONGODB, "giveaways"));
    }

    @Override
    public Serializer<CurrentGiveaway> serializer() {
        return (giveaway, json, gson) -> {
            json.addProperty("_id", String.valueOf(giveaway.messageId()));
            json.addProperty("channelId", String.valueOf(giveaway.channelId()));
            json.addProperty("serverId", String.valueOf(giveaway.serverId()));
            json.addProperty("startTime", String.valueOf(giveaway.startTime()));
            json.addProperty("endTime", String.valueOf(giveaway.endTime()));
            json.addProperty("winnerAmount", String.valueOf(giveaway.winnerAmount()));
            json.addProperty("presetName", giveaway.presetName());
            json.addProperty("giveawayItem", giveaway.giveawayItem());
            json.addProperty("enteredUsers", gson.toJson(giveaway.enteredUsers()));
            return json;
        };
    }

    @Override
    public Deserializer<CurrentGiveaway> deserializer() {
        return (json, gson) -> {
            long messageId = json.get("_id").getAsLong();
            long channelId = json.get("channelId").getAsLong();
            long serverId = json.get("serverId").getAsLong();
            long startTime = json.get("startTime").getAsLong();
            long endTime = json.get("endTime").getAsLong();
            int winnerAmount = json.get("winnerAmount").getAsInt();
            String presetName = json.get("presetName").getAsString();
            String giveawayItem = json.get("giveawayItem").getAsString();
            Set<Long> enteredUsers = Sets.newConcurrentHashSet(gson.fromJson(json.get("enteredUsers").getAsString(), new TypeToken<HashSet<Long>>(){}.getType()));
            return new CurrentGiveaway(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem, enteredUsers);
        };
    }

    @Override
    public CurrentGiveaway create(String id) {
        return null;
    }

    @Override
    public CurrentGiveaway load(UUID key) {
        return super.load(key.toString());
    }

    @Override
    public void save(UUID key, CurrentGiveaway value) {
        super.save(key.toString(), value);
    }
}
