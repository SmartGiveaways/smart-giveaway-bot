package pink.zak.giveawaybot.storage;

import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.storage.settings.StorageType;
import pink.zak.giveawaybot.service.storage.storage.Storage;
import pink.zak.giveawaybot.service.storage.storage.serialization.Deserializer;
import pink.zak.giveawaybot.service.storage.storage.serialization.Serializer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerStorage extends Storage<Server> {
    private final GiveawayBot bot;

    public ServerStorage(GiveawayBot bot) {
        super(bot, factory -> factory.create(StorageType.MONGODB, "server-settings"));
        this.bot = bot;
    }

    @Override
    public Serializer<Server> serializer() {
        return (server, json, gson) -> {
            json.addProperty("_id", String.valueOf(server.getId()));
            json.addProperty("presets", gson.toJson(this.serializePresets(server.getPresets())));
            json.addProperty("activeGiveaways", gson.toJson(server.getActiveGiveaways()));
            json.addProperty("managerRoles", gson.toJson(server.getManagerRoles()));
            return json;
        };
    }

    @Override
    public Deserializer<Server> deserializer() {
        return (json, gson) -> {
            long id = json.get("_id").getAsLong();
            Map<String, Preset> presets = this.deserializePresets(id, gson.fromJson(json.get("presets").getAsString(), new TypeToken<ConcurrentHashMap<String, HashMap<Setting, String>>>(){}.getType()));
            Map<Long, UUID> activeGiveaways = gson.fromJson(json.get("activeGiveaways").getAsString(), new TypeToken<ConcurrentHashMap<Long, UUID>>(){}.getType());
            Set<Long> roleIds = gson.fromJson(json.get("managerRoles").getAsString(), new TypeToken<HashSet<Long>>(){}.getType());
            return new Server(this.bot, id, activeGiveaways, presets, roleIds);
        };
    }

    @Override
    public Server create(String id) {
        return new Server(this.bot, Long.parseLong(id));
    }

    private Map<String, EnumMap<Setting, String>> serializePresets(Map<String, Preset> unserialized) {
        Map<String, EnumMap<Setting, String>> serialized = Maps.newHashMap();
        for (Map.Entry<String, Preset> preset : unserialized.entrySet()) {
            serialized.put(preset.getKey(), preset.getValue().serialized());
        }
        return serialized;
    }

    private Map<String, Preset> deserializePresets(long guildId, Map<String, Map<Setting, String>> serialized) {
        if (serialized.isEmpty()) {
            return Maps.newConcurrentMap();
        }
        Guild guild = this.bot.getJda().getGuildById(guildId);
        Map<String, Preset> deserialized = Maps.newHashMap();
        for (Map.Entry<String, Map<Setting, String>> entry : serialized.entrySet()) {
            EnumMap<Setting, Object> enumMap = Maps.newEnumMap(Setting.class);
            for (Map.Entry<Setting, String> innerEntry : entry.getValue().entrySet()) {
                enumMap.put(innerEntry.getKey(), innerEntry.getKey().parseAny(innerEntry.getValue(), guild));
            }
            deserialized.put(entry.getKey(), new Preset(entry.getKey(), enumMap));
        }
        return deserialized;
    }
}
