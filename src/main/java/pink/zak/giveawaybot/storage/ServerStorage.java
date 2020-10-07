package pink.zak.giveawaybot.storage;

import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
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
            json.addProperty("managerRole", server.getManagerRoleId());
            return json;
        };
    }

    @Override
    public Deserializer<Server> deserializer() {
        return (json, gson) -> {
            long id = json.get("_id").getAsLong();
            Map<String, Preset> presets = this.deserializePresets(gson.fromJson(json.get("presets").getAsString(), new TypeToken<HashMap<String, HashMap<Setting, String>>>(){}.getType()));
            Map<Long, UUID> activeGiveaways = gson.fromJson(json.get("activeGiveaways").getAsString(), new TypeToken<ConcurrentHashMap<Long, UUID>>(){}.getType());
            long roleId = json.get("managerRole") == null ? 0 : json.get("managerRole").getAsLong();
            return new Server(this.bot, id, activeGiveaways, presets, roleId);
        };
    }

    @Override
    public Server create(String id) {
        Server server = new Server(this.bot, Long.parseLong(id));
        System.out.println("Creating server. " + server);
        return server;
    }

    private Map<String, EnumMap<Setting, String>> serializePresets(Map<String, Preset> unserialized) {
        Map<String, EnumMap<Setting, String>> serialized = Maps.newHashMap();
        for (Map.Entry<String, Preset> preset : unserialized.entrySet()) {
            serialized.put(preset.getKey(), preset.getValue().serialized());
        }
        return serialized;
    }

    private Map<String, Preset> deserializePresets(Map<String, Map<Setting, String>> serialized) {
        Map<String, Preset> deserialized = Maps.newHashMap();
        for (Map.Entry<String, Map<Setting, String>> entry : serialized.entrySet()) {
            EnumMap<Setting, Object> enumMap = Maps.newEnumMap(Setting.class);
            for (Map.Entry<Setting, String> innerEntry : entry.getValue().entrySet()) {
                enumMap.put(innerEntry.getKey(), innerEntry.getKey().parse(innerEntry.getValue()));
            }
            deserialized.put(entry.getKey(), new Preset(entry.getKey(), enumMap));
        }
        return deserialized;
    }
}
