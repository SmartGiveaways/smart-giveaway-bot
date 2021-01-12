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
        // List<Long> timings = Lists.newArrayList(System.nanoTime());
        return document -> {
            long messageId = document.getLong("_id");
            // timings.add(System.nanoTime());
            long channelId = document.getLong("channelId");
            // timings.add(System.nanoTime());
            long serverId = document.getLong("serverId");
            //  timings.add(System.nanoTime());
            long startTime = document.getLong("startTime");
            //   timings.add(System.nanoTime());
            long endTime = document.getLong("endTime");
            //  timings.add(System.nanoTime());
            int winnerAmount = document.getInteger("winnerAmount");
            //   timings.add(System.nanoTime());
            String presetName = document.getString("presetName");
            //    timings.add(System.nanoTime());
            String giveawayItem = document.getString("giveawayItem");
            //   timings.add(System.nanoTime());

            /* for (int i = 1; i < timings.size(); i++) {
             GiveawayBot.logger().info("{}) Took {}ns", i, timings.get(i) - timings.get(i - 1));
             }*/
            return new PartialFinishedGiveaway(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem);
        };
    }

    @Override
    public PartialFinishedGiveaway create(Long id) {
        return null;
    }
}
