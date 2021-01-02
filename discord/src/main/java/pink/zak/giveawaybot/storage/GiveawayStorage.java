package pink.zak.giveawaybot.storage;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoSerializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoStorage;

import java.util.HashSet;
import java.util.Set;

public class GiveawayStorage extends MongoStorage<Long, CurrentGiveaway> {
    private final Gson gson = new Gson();

    public GiveawayStorage(GiveawayBot bot) {
        super(bot, "giveaways", "_id");
    }

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

    @Override
    public MongoDeserializer<CurrentGiveaway> deserializer() {
        return document -> {
            long messageId = document.getLong("_id");
            long channelId = document.getLong("channelId");
            long serverId = document.getLong("serverId");
            long startTime = document.getLong("startTime");
            long endTime = document.getLong("endTime");
            int winnerAmount = document.getInteger("winnerAmount");
            String presetName = document.getString("presetName");
            String giveawayItem = document.getString("giveawayItem");
            Set<Long> enteredUsers = Sets.newConcurrentHashSet(this.gson.fromJson(document.getString("enteredUsers"), new TypeToken<HashSet<Long>>() {}.getType()));
            return new CurrentGiveaway(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem, enteredUsers);
        };
    }

    @Override
    public CurrentGiveaway create(Long id) {
        return null;
    }
}
