package pink.zak.giveawaybot.service.storage.backends.mongodb;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import pink.zak.giveawaybot.service.bot.SimpleBot;
import pink.zak.giveawaybot.service.storage.Backend;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MongoBackend implements Backend {
    private final MongoConnectionFactory connectionFactory;
    private final MongoCollection<Document> collection;

    public MongoBackend(SimpleBot bot, String collectionName) {
        this.connectionFactory = new MongoConnectionFactory(bot.getStorageSettings());
        this.collection = this.connectionFactory.getCollection(collectionName);
    }

    @Override
    public JsonObject load(String id) {
        try {
            return JsonParser.parseString(this.collection.find(new BasicDBObject("_id", id)).first().toJson()).getAsJsonObject();
        } catch (NullPointerException ex) {
            return null;
        }
    }

    @Override
    public JsonObject load(Map<String, String> valuePairs) {
        try {
            return JsonParser.parseString(this.collection.find(new BasicDBObject(valuePairs)).first().toJson()).getAsJsonObject();
        } catch (NullPointerException ex) {
            return null;
        }
    }

    @Override
    public void save(String id, JsonObject json) {
        Document document = new Document(BasicDBObject.parse(json.toString()));
        Document foundDocument = this.collection.find(Filters.eq("_id", id)).first();
        if (foundDocument != null) {
            this.update(id, document);
            return;
        }
        this.collection.insertOne(document);
    }

    @Override
    public void save(Map<String, String> valuePairs, JsonObject json) {
        Document document = new Document(BasicDBObject.parse(json.toString()));
        BasicDBObject searcher = this.getSearchFromMap(valuePairs);
        Document foundDocument = this.collection.find(searcher).first();
        if (foundDocument != null) {
            this.update(searcher, document);
            return;
        }
        System.out.println("Adding new document");
        this.collection.insertOne(document);
    }

    private void update(String id, Document document) {
        this.collection.findOneAndReplace(new Document("_id", id), document);
    }

    private void update(BasicDBObject searcher, Document document) {
        this.collection.findOneAndReplace(searcher, document);
    }

    @Override
    public Set<JsonObject> loadAll() {
        Set<JsonObject> jsonObjects = Sets.newConcurrentHashSet();
        for (Document document : this.collection.find()) {
            jsonObjects.add(JsonParser.parseString(document.toJson()).getAsJsonObject());
        }
        return jsonObjects;
    }

    @Override
    public void delete(String id) {
        this.collection.findOneAndDelete(new Document("_id", id));
    }

    @Override
    public void close() {
        this.connectionFactory.close();
    }

    private BasicDBObject getSearchFromMap(Map<String, String> valuePairs) {
        List<BasicDBObject> searchers = Lists.newArrayList();
        for (Map.Entry<String, String> entry : valuePairs.entrySet()) {
            searchers.add(new BasicDBObject(entry.getKey(), entry.getValue()));
        }
        return new BasicDBObject("$and", searchers);
    }
}
