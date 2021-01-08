package pink.zak.giveawaybot.discord.service.storage.mongo;

import org.bson.Document;

@FunctionalInterface
public interface MongoDeserializer<T> {

    T apply(Document document);
}
