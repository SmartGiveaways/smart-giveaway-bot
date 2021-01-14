package pink.zak.giveawaybot.discord.storage.finishedgiveaway;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.models.giveaway.finished.PartialFinishedGiveaway;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoSerializer;

public class PartialFinishedGiveawayStorage extends FinishedGiveawayStorage<PartialFinishedGiveaway> {

    public PartialFinishedGiveawayStorage(GiveawayBot bot) {
        super(bot);
    }

    @Override
    public MongoSerializer<PartialFinishedGiveaway> serializer() {
        return (giveaway, document) -> {
            document.put("_id", giveaway.getMessageId());
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
    public MongoDeserializer<PartialFinishedGiveaway> deserializer() {
        return document -> {
            long messageId = document.getLong("_id");
            long channelId = document.getLong("channelId");
            long serverId = document.getLong("serverId");
            long startTime = document.getLong("startTime");
            long endTime = document.getLong("endTime");
            int winnerAmount = document.getInteger("winnerAmount");
            String presetName = document.getString("presetName");
            String giveawayItem = document.getString("giveawayItem");
            return new PartialFinishedGiveaway(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem);
        };
    }

    @Override
    public PartialFinishedGiveaway create(Long id) {
        return null;
    }
}
