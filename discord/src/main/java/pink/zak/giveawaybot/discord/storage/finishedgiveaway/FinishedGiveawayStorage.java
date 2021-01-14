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
        for (Document document : super.collection.find(Filters.eq("serverId", server.getId()))) {
            long id = document.getLong("_id");
            if (!targeted.contains(id)) {
                continue;
            }
            giveaways.add(this.deserializer().apply(document));
        }
        Collections.sort(giveaways); // Won't be ordered from MongoDB
        return giveaways;
    }

    @Override
    public T create(Long id) {
        return null;
    }
}
