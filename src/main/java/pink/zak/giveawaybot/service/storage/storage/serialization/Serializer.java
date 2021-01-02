package pink.zak.giveawaybot.service.storage.storage.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@FunctionalInterface
public interface Serializer<T> {

    JsonObject apply(T object, JsonObject json, Gson gson);
}
