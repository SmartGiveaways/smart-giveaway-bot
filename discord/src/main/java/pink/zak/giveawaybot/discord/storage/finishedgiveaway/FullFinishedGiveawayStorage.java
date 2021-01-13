package pink.zak.giveawaybot.discord.storage.finishedgiveaway;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.models.giveaway.finished.FullFinishedGiveaway;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoSerializer;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FullFinishedGiveawayStorage extends FinishedGiveawayStorage<FullFinishedGiveaway> {
    private final Gson gson = new Gson();

    public FullFinishedGiveawayStorage(GiveawayBot bot) {
        super(bot);
    }

    @Override
    public MongoSerializer<FullFinishedGiveaway> serializer() {
        return (giveaway, document) -> {
            document.put("_id", giveaway.getMessageId());
            document.put("channelId", giveaway.getChannelId());
            document.put("serverId", giveaway.getServerId());
            document.put("startTime", giveaway.getStartTime());
            document.put("endTime", giveaway.getEndTime());
            document.put("winnerAmount", giveaway.getWinnerAmount());
            document.put("presetName", giveaway.getPresetName());
            document.put("giveawayItem", giveaway.getGiveawayItem());
            document.put("totalEntries", giveaway.getTotalEntries().toString());
            document.put("userEntries", this.gson.toJson(giveaway.getUserEntries()));
            document.put("winners", this.gson.toJson(giveaway.getWinners()));
            return document;
        };
    }

    @Override
    public MongoDeserializer<FullFinishedGiveaway> deserializer() {
        List<Long> timings = Lists.newArrayList(System.nanoTime());
        return document -> {
            long messageId = document.getLong("_id");
            timings.add(System.nanoTime());
            long channelId = document.getLong("channelId");
            timings.add(System.nanoTime());
            long serverId = document.getLong("serverId");
            timings.add(System.nanoTime());
            long startTime = document.getLong("startTime");
            timings.add(System.nanoTime());
            long endTime = document.getLong("endTime");
            timings.add(System.nanoTime());
            int winnerAmount = document.getInteger("winnerAmount");
            timings.add(System.nanoTime());
            String presetName = document.getString("presetName");
            timings.add(System.nanoTime());
            String giveawayItem = document.getString("giveawayItem");
            timings.add(System.nanoTime());
            BigInteger totalEntries = new BigInteger(document.getString("totalEntries"));
            timings.add(System.nanoTime());
            Map<Long, BigInteger> userEntries = this.gson.fromJson(document.getString("userEntries"), new TypeToken<HashMap<Long, BigInteger>>() {}.getType());
            timings.add(System.nanoTime());
            Set<Long> winners = Sets.newConcurrentHashSet(this.gson.fromJson(document.getString("winners"), new TypeToken<HashSet<Long>>() {}.getType()));
            timings.add(System.nanoTime());

            for (int i = 1; i < timings.size(); i++) {
                GiveawayBot.logger().info("{}) Took {}ns", i, timings.get(i) - timings.get(i - 1));
            }
            return new FullFinishedGiveaway(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem, totalEntries, userEntries, winners);
        };
    }

    @Override
    public FullFinishedGiveaway create(Long id) {
        return null;
    }

    public FullFinishedGiveaway create(CurrentGiveaway giveaway, BigInteger totalEntries, Map<Long, BigInteger> userEntries, Set<Long> winners) {
        FullFinishedGiveaway finishedGiveaway = new FullFinishedGiveaway(giveaway, totalEntries, userEntries, winners);
        this.save(finishedGiveaway);
        return finishedGiveaway;
    }
}