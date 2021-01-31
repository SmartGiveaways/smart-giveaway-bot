package pink.zak.giveawaybot.discord.data.storage;

import com.mongodb.client.model.Filters;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoSerializer;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoStorage;

import java.util.UUID;

public class ScheduledGiveawayStorage extends MongoStorage<UUID, ScheduledGiveaway> {

    public ScheduledGiveawayStorage(GiveawayBot bot) {
        super(bot.getThreadManager(), bot.getMongoConnectionFactory(), "scheduled-giveaways", "_id");
    }

    @Override
    public MongoSerializer<ScheduledGiveaway> serializer() {
        return (giveaway, document) -> {
            document.put("_id", giveaway.getUuid().toString());
            document.put("channelId", giveaway.getChannelId());
            document.put("serverId", giveaway.getServerId());
            document.put("startTime", giveaway.getStartTime());
            document.put("endTime", giveaway.getEndTime());
            document.put("winnerAmount", giveaway.getWinnerAmount());
            document.put("presetName", giveaway.getPresetName());
            document.put("giveawayItem", giveaway.getGiveawayItem());
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
