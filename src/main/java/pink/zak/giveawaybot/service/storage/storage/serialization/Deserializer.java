package pink.zak.giveawaybot.service.storage.storage.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@FunctionalInterface
public interface Deserializer<T> {

    T apply(JsonObject json, Gson gson);
}
