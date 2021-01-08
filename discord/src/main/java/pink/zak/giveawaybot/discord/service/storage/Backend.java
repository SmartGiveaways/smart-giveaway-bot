package pink.zak.giveawaybot.discord.service.storage;

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

public interface Backend {

    JsonObject load(String id);

    JsonObject load(Map<String, String> valuePairs);

    void save(String id, JsonObject json);

    void save(Map<String, String> valuePairs, JsonObject json);

    Set<JsonObject> loadAll();

    void delete(String id);

    void close();
}
