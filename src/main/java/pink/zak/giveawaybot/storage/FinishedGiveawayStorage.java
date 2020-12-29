package pink.zak.giveawaybot.storage;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoSerializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoStorage;

import java.math.BigInteger;
import java.util.*;

public class FinishedGiveawayStorage extends MongoStorage<Long, FinishedGiveaway> {
    private final Gson gson = new Gson();

    public FinishedGiveawayStorage(GiveawayBot bot) {
        super(bot, "finished-giveaways", "_id");
    }

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
        for (Document document : super.collection.find(Filters.eq("serverId", server.id()))) {
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
}
