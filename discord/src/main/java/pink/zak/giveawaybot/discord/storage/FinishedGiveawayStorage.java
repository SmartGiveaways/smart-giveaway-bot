package pink.zak.giveawaybot.discord.storage;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoSerializer;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoStorage;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FinishedGiveawayStorage extends MongoStorage<Long, FinishedGiveaway> {
    private final Gson gson = new Gson();

    public FinishedGiveawayStorage(GiveawayBot bot) {
        super(bot, "finished-giveaways", "_id");
    }

    @Override
    public MongoSerializer<FinishedGiveaway> serializer() {
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
            Map<Long, BigInteger> userEntries = gson.fromJson(document.getString("userEntries"), new TypeToken<HashMap<Long, BigInteger>>() {}.getType());
            Set<Long> winners = Sets.newConcurrentHashSet(gson.fromJson(document.getString("winners"), new TypeToken<HashSet<Long>>() {}.getType()));
            return new FinishedGiveaway(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem, totalEntries, userEntries, winners);
        };
    }

    public Set<FinishedGiveaway> loadAll(Server server, Set<Long> targeted) {
        Set<FinishedGiveaway> giveaways = Sets.newHashSet();
        for (Document document : super.collection.find(Filters.eq("serverId", server.getId()))) {
            long id = document.getLong("_id");
            if (!targeted.contains(id)) {
                continue;
            }
            giveaways.add(this.deserializer().apply(document));
        }
        return giveaways;
    }

    @Override
    public FinishedGiveaway create(Long id) {
        return null;
    }

    public FinishedGiveaway create(CurrentGiveaway giveaway, BigInteger totalEntries, Map<Long, BigInteger> userEntries, Set<Long> winners) {
        FinishedGiveaway finishedGiveaway = new FinishedGiveaway(giveaway, totalEntries, userEntries, winners);
        this.save(finishedGiveaway);
        return finishedGiveaway;
    }
}
