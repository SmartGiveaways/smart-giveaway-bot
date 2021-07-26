package pink.zak.giveawaybot.data.storage;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoSerializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoStorage;

import java.util.Set;

public class GiveawayStorage extends MongoStorage<Long, CurrentGiveaway> {
    private final Gson gson = new Gson();

    public GiveawayStorage(GiveawayBot bot) {
        super(bot.getThreadManager(), bot.getMongoConnectionFactory(), "giveaways", "_id");
    }

    @Override
    public MongoSerializer<CurrentGiveaway> serializer() {
        return (giveaway, document) -> {
            document.put("_id", giveaway.getMessageId());
            document.put("channelId", giveaway.getChannelId());
            document.put("serverId", giveaway.getServerId());
            document.put("startTime", giveaway.getStartTime());
            document.put("endTime", giveaway.getEndTime());
            document.put("winnerAmount", giveaway.getWinnerAmount());
            document.put("presetName", giveaway.getPresetName());
            document.put("giveawayItem", giveaway.getGiveawayItem());
            document.put("enteredUsers", giveaway.getEnteredUsers());
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
            Set<Long> enteredUsers = Sets.newConcurrentHashSet(document.getList("enteredUsers", Long.class));
            return new CurrentGiveaway(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem, enteredUsers);
        };
    }

    @Override
    public CurrentGiveaway create(Long id) {
        return null;
    }
}
