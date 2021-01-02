package pink.zak.giveawaybot.service.storage.adapter;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public interface Adapter<T> extends JsonSerializer<T>, JsonDeserializer<T> {

}
