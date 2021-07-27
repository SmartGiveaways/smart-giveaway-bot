package pink.zak.giveawaybot.data.storage.finishedgiveaway;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.data.models.giveaway.finished.FullFinishedGiveaway;
import pink.zak.giveawaybot.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoSerializer;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FullFinishedGiveawayStorage extends FinishedGiveawayStorage<FullFinishedGiveaway> {

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
            document.put("userEntries", this.createUserEntries(giveaway));
            document.put("winners", giveaway.getWinners());
            return document;
        };
    }

    @Override
    public MongoDeserializer<FullFinishedGiveaway> deserializer() {
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
            Map<Long, Integer> userEntries = this.createUserEntries(document);
            Set<Long> winners = document.containsKey("winners") ? Sets.newConcurrentHashSet(document.getList("winners", Long.class)) : Sets.newConcurrentHashSet();
            return new FullFinishedGiveaway(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem, totalEntries, userEntries, winners);
        };
    }

    private Set<BasicDBObject> createUserEntries(FullFinishedGiveaway giveaway) {
        Set<BasicDBObject> documents = Sets.newHashSet();
        for (Map.Entry<Long, Integer> entry : giveaway.getUserEntries().entrySet()) {
            BasicDBObject document = new BasicDBObject();
            document.put("id", entry.getKey());
            document.put("entries", entry.getValue());

            documents.add(document);
        }
        return documents;
    }

    private Map<Long, Integer> createUserEntries(Document giveawayDocument) {
        Map<Long, Integer> map = Maps.newHashMap();

        List<Document> userDocuments = giveawayDocument.getList("userEntries", Document.class);
        for (Document userDocument : userDocuments) {
            long id = userDocument.getLong("id");
            int entries = userDocument.getInteger("entries");

            map.put(id, entries);
        }
        return map;
    }

    @Override
    public FullFinishedGiveaway create(Long id) {
        return null;
    }

    public FullFinishedGiveaway create(CurrentGiveaway giveaway, BigInteger totalEntries, Map<Long, Integer> userEntries, Set<Long> winners) {
        FullFinishedGiveaway finishedGiveaway = new FullFinishedGiveaway(giveaway, totalEntries, userEntries, winners);
        this.save(finishedGiveaway);
        return finishedGiveaway;
    }
}
