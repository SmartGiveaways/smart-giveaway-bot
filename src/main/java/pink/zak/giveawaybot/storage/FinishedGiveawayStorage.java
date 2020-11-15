package pink.zak.giveawaybot.storage;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.service.cache.options.CacheStorage;
import pink.zak.giveawaybot.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoSerializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoStorage;

import java.math.BigInteger;
import java.util.*;

public class FinishedGiveawayStorage extends MongoStorage<Long, FinishedGiveaway> implements CacheStorage<Long, FinishedGiveaway> {
    private final Gson gson = new Gson();

    public FinishedGiveawayStorage(GiveawayBot bot) {
        super(bot, "finished-giveaways", "_id");
    }

    /*@Override
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
    }*/

    @Override
    public MongoSerializer<FinishedGiveaway> serializer() {
        return (giveaway, document) -> {
            document.put("_id", giveaway.messageId());
            document.put("channelId", giveaway.channelId());
            document.put("serverId", giveaway.serverId());
            document.put("startTime", giveaway.startTime());
            document.put("endTime", giveaway.endTime());
            document.put("winnerAmount", giveaway.winnerAmount());
            document.put("presetName", giveaway.presetName());
            document.put("giveawayItem", giveaway.giveawayItem());
            document.put("totalEntries", giveaway.totalEntries().toString());
            document.put("userEntries", gson.toJson(giveaway.userEntries()));
            document.put("winners", gson.toJson(giveaway.winners()));
            return document;
        };
    }

    /*@Override
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
    }*/

    @Override
    public MongoDeserializer<FinishedGiveaway> deserializer() {
        return document -> {
            long messageId = document.getLong("_id");
            long channelId = document.getLong("channelId");
            long serverId = document.getLong("serverId");
            long startTime = document.getLong("startTime");
            long endTime = document.getLong("endTime");
            int winnerAmount = document.getInteger("winnerAmount");
            String presetName = document.getString("presetName");
            String giveawayItem = document.getString("giveawayItem");
            BigInteger totalEntries = new BigInteger(document.getString("totalEntries"));
            Map<Long, BigInteger> userEntries = gson.fromJson(document.getString("userEntries"), new TypeToken<HashMap<Long, BigInteger>>(){}.getType());
            Set<Long> winners = Sets.newConcurrentHashSet(gson.fromJson(document.getString("winners"), new TypeToken<HashSet<Long>>(){}.getType()));
            return new FinishedGiveaway(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem, totalEntries, userEntries, winners);
        };
    }

    @Override
    public FinishedGiveaway create(Long id) {
        return null;
    }
}
