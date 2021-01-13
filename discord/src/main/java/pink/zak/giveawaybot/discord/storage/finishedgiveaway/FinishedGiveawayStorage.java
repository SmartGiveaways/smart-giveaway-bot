package pink.zak.giveawaybot.discord.storage.finishedgiveaway;

import com.google.common.collect.Lists;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.models.giveaway.finished.PartialFinishedGiveaway;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoStorage;

import java.util.Collections;
import java.util.List;

public abstract class FinishedGiveawayStorage<T extends PartialFinishedGiveaway> extends MongoStorage<Long, T> {

    public FinishedGiveawayStorage(GiveawayBot bot) {
        super(bot, "finished-giveaways", "_id");
    }

    public List<T> loadAll(Server server, List<Long> targeted) {
        List<T> giveaways = Lists.newArrayList();
        long deserializeTime = 0;
        for (Document document : super.collection.find(Filters.eq("serverId", server.getId()))) {
            long id = document.getLong("_id");
            if (!targeted.contains(id)) {
                continue;
            }
            long startD = System.currentTimeMillis();
            giveaways.add(this.deserializer().apply(document));
            deserializeTime += System.currentTimeMillis() - startD;
        }
        long startC = System.currentTimeMillis();
        Collections.sort(giveaways); // Won't be ordered from MongoDB
        GiveawayBot.logger().info("Took {}ms to do C", System.currentTimeMillis() - startC);
        GiveawayBot.logger().info("Took {}ms to do D", deserializeTime);
        return giveaways;
    }

    @Override
    public T create(Long id) {
        return null;
    }
}