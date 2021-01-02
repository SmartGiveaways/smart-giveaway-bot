package pink.zak.giveawaybot.service.storage.mongo;

import org.bson.Document;

@FunctionalInterface
public interface MongoSerializer<T> {

    Document apply(T object, Document document);
}
