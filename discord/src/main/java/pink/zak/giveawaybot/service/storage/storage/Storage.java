package pink.zak.giveawaybot.service.storage.storage;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.storage.Backend;
import pink.zak.giveawaybot.service.storage.BackendFactory;
import pink.zak.giveawaybot.service.storage.storage.serialization.Deserializer;
import pink.zak.giveawaybot.service.storage.storage.serialization.Serializer;
import pink.zak.giveawaybot.service.storage.adapter.Adapter;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class Storage<T> {
    protected final Backend backend;
    protected GsonBuilder gsonBuilder;
    protected Gson gson;

    public Storage(GiveawayBot bot, Function<BackendFactory, Backend> backend) {
        this.backend = backend.apply(new BackendFactory(bot));
        this.gsonBuilder = new GsonBuilder();
        this.gson = new Gson();
    }

    public Storage(Backend backend) {
        this.backend = backend;
    }

    public abstract Serializer<T> serializer();

    public abstract Deserializer<T> deserializer();

    public abstract T create(String id);

    public void addAdapter(Class<?> clazz, Adapter<?> adapter) {
        this.gsonBuilder.registerTypeAdapter(clazz, adapter);
    }

    public void saveChanges() {
        this.gson = this.gsonBuilder.create();
    }

    public T load(String id) {
        JsonObject json = this.backend.load(id);
        return json == null ? this.create(id) : this.deserializer().apply(json, this.gson);
    }

    public T load(Map<String, String> valuePairs, int arrayIdPosition) {
        JsonObject json = this.backend.load(valuePairs);
        return json == null ? this.create(valuePairs.values().toArray(new String[]{})[arrayIdPosition]) : this.deserializer().apply(json, this.gson);
    }

    public T load(Map<String, String> valuePairs) {
        return this.load(valuePairs, 0);
    }

    public T save(String id, T object) {
        this.backend.save(id, this.serializer().apply(object, new JsonObject(), this.gson));
        return object;
    }

    public T save(Map<String, String> values, T object) {
        this.backend.save(values, this.serializer().apply(object, new JsonObject(), this.gson));
        return object;
    }

    public void delete(String id) {
        this.backend.delete(id);
    }

    public Set<T> loadAll() {
        Set<T> all = Sets.newHashSet();
        for (JsonObject json : this.backend.loadAll()) {
            all.add(this.deserializer().apply(json, this.gson));
        }
        return all;
    }

    public void closeBack() {
        this.backend.close();
    }
}
