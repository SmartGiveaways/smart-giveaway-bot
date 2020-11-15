package pink.zak.giveawaybot.storage;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.service.cache.options.CacheStorage;
import pink.zak.giveawaybot.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoSerializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoStorage;

import java.util.HashSet;
import java.util.Set;

public class GiveawayStorage extends MongoStorage<Long, CurrentGiveaway> implements CacheStorage<Long, CurrentGiveaway> {
    private final Gson gson = new Gson();

    public GiveawayStorage(GiveawayBot bot) {
        super(bot, "giveaways", "_id");
    }

    /*@Override
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
    }*/

    @Override
    public MongoSerializer<CurrentGiveaway> serializer() {
        return (giveaway, document) -> {
            document.put("_id", giveaway.messageId());
            document.put("channelId", giveaway.channelId());
            document.put("serverId", giveaway.serverId());
            document.put("startTime", giveaway.startTime());
            document.put("endTime", giveaway.endTime());
            document.put("winnerAmount", giveaway.winnerAmount());
            document.put("presetName", giveaway.presetName());
            document.put("giveawayItem", giveaway.giveawayItem());
            document.put("enteredUsers", this.gson.toJson(giveaway.enteredUsers()));
            return document;
        };
    }

    /*@Override
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
    }*/

    @Override
    public MongoDeserializer<CurrentGiveaway> deserializer() {
        return document -> {
            long messageId = document.getLong("messageId");
            long channelId = document.getLong("channelId");
            long serverId = document.getLong("serverId");
            long startTime = document.getLong("startTime");
            long endTime = document.getLong("endTime");
            int winnerAmount = document.getInteger("winnerAmount");
            String presetName = document.getString("presetName");
            String giveawayItem = document.getString("giveawayItem");
            Set<Long> enteredUsers = Sets.newConcurrentHashSet(this.gson.fromJson(document.getString("enteredUsers"), new TypeToken<HashSet<Long>>(){}.getType()));
            return new CurrentGiveaway(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem, enteredUsers);
        };
    }

    @Override
    public CurrentGiveaway create(Long id) {
        return null;
    }
}
