package pink.zak.giveawaybot.storage;

import com.google.common.collect.Sets;
import com.google.gson.reflect.TypeToken;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.service.cache.options.CacheLoader;
import pink.zak.giveawaybot.service.storage.settings.StorageType;
import pink.zak.giveawaybot.service.storage.storage.Storage;
import pink.zak.giveawaybot.service.storage.storage.serialization.Deserializer;
import pink.zak.giveawaybot.service.storage.storage.serialization.Serializer;

import java.math.BigInteger;
import java.util.*;

public class FinishedGiveawayStorage extends Storage<FinishedGiveaway> implements CacheLoader<UUID, FinishedGiveaway> {

    public FinishedGiveawayStorage(GiveawayBot bot) {
        super(bot, factory -> factory.create(StorageType.MONGODB, "finished-giveaways"));
    }

    @Override
    public Serializer<FinishedGiveaway> serializer() {
        return (giveaway, json, gson) -> {
            json.addProperty("_id", String.valueOf(giveaway.messageId()));
            json.addProperty("channelId", String.valueOf(giveaway.channelId()));
            json.addProperty("serverId", String.valueOf(giveaway.serverId()));
            json.addProperty("startTime", String.valueOf(giveaway.startTime()));
            json.addProperty("endTime", String.valueOf(giveaway.endTime()));
            json.addProperty("winnerAmount", String.valueOf(giveaway.winnerAmount()));
            json.addProperty("presetName", giveaway.presetName());
            json.addProperty("giveawayItem", giveaway.giveawayItem());
            json.addProperty("totalEntries", giveaway.totalEntries().toString());
            json.addProperty("userEntries", gson.toJson(giveaway.userEntries()));
            json.addProperty("winners", gson.toJson(giveaway.winners()));
            return json;
        };
    }

    @Override
    public Deserializer<FinishedGiveaway> deserializer() {
        return (json, gson) -> {
            long messageId = json.get("_id").getAsLong();
            long channelId = json.get("channelId").getAsLong();
            long serverId = json.get("serverId").getAsLong();
            long startTime = json.get("startTime").getAsLong();
            long endTime = json.get("endTime").getAsLong();
            int winnerAmount = json.get("winnerAmount").getAsInt();
            String presetName = json.get("presetName").getAsString();
            String giveawayItem = json.get("giveawayItem").getAsString();
            BigInteger totalEntries = new BigInteger(json.get("totalEntries").getAsString());
            Map<Long, BigInteger> userEntries = gson.fromJson(json.get("userEntries").getAsString(), new TypeToken<HashMap<Long, BigInteger>>(){}.getType());
            Set<Long> winners = Sets.newConcurrentHashSet(gson.fromJson(json.get("winners").getAsString(), new TypeToken<HashSet<Long>>(){}.getType()));
            return new FinishedGiveaway(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem, totalEntries, userEntries, winners);
        };
    }

    @Override
    public FinishedGiveaway create(String id) {
        return null;
    }

    @Override
    public FinishedGiveaway load(UUID key) {
        return super.load(key.toString());
    }
}
