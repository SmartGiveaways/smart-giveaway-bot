package pink.zak.giveawaybot.storage;

import com.mongodb.client.model.Filters;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoSerializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoStorage;

import java.util.UUID;

public class ScheduledGiveawayStorage extends MongoStorage<UUID, ScheduledGiveaway> {

    public ScheduledGiveawayStorage(GiveawayBot bot) {
        super(bot, "scheduled-giveaways", "_id");
    }

    @Override
    public MongoSerializer<ScheduledGiveaway> serializer() {
        return (giveaway, document) -> {
            document.put("_id", giveaway.uuid().toString());
            document.put("channelId", giveaway.channelId());
            document.put("serverId", giveaway.serverId());
            document.put("startTime", giveaway.startTime());
            document.put("endTime", giveaway.endTime());
            document.put("winnerAmount", giveaway.winnerAmount());
            document.put("presetName", giveaway.presetName());
            document.put("giveawayItem", giveaway.giveawayItem());
            return document;
        };
    }

    @Override
    public MongoDeserializer<ScheduledGiveaway> deserializer() {
        return document -> {
            UUID id = UUID.fromString(document.getString("_id"));
            long channelId = document.getLong("channelId");
            long serverId = document.getLong("serverId");
            long startTime = document.getLong("startTime");
            long endTime = document.getLong("endTime");
            int winnerAmount = document.getInteger("winnerAmount");
            String presetName = document.getString("presetName");
            String giveawayItem = document.getString("giveawayItem");
            return new ScheduledGiveaway(id, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem);
        };
    }

    @Override
    public ScheduledGiveaway create(UUID id) {
        return null;
    }

    @Override
    public void delete(UUID key) {
        super.delete(Filters.eq("_id", key.toString()));
    }
}
