package pink.zak.giveawaybot.discord.storage;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.enums.Setting;
import pink.zak.giveawaybot.discord.models.Preset;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoSerializer;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoStorage;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerStorage extends MongoStorage<Long, Server> {
    private final GiveawayBot bot;
    private final Gson gson = new Gson();

    public ServerStorage(GiveawayBot bot) {
        super(bot.getThreadManager(), bot.getMongoConnectionFactory(), "server-settings", "_id");
        this.bot = bot;
    }

    @Override
    public MongoSerializer<Server> serializer() {
        return (server, document) -> {
            document.put("_id", server.getId());
            document.put("presets", this.gson.toJson(this.serializePresets(server.getPresets())));
            document.put("activeGiveaways", this.gson.toJson(server.getActiveGiveaways()));
            document.put("finishedGiveaways", this.gson.toJson(server.getFinishedGiveaways()));
            document.put("scheduledGiveaways", this.gson.toJson(server.getScheduledGiveaways()));
            document.put("managerRoles", this.gson.toJson(server.getManagerRoles()));
            document.put("bannedUsers", this.gson.toJson(server.getBannedUsers()));
            document.put("premium", server.getPremiumExpiry());
            document.put("language", server.getLanguage());
            return document;
        };
    }

    @Override
    public MongoDeserializer<Server> deserializer() {
        return document -> {
            long id = document.getLong("_id");
            Map<String, Preset> presets = this.deserializePresets(id, this.gson.fromJson(document.getString("presets"), new TypeToken<ConcurrentHashMap<String, HashMap<Setting, String>>>() {}.getType()));
            List<Long> activeGiveaways = this.gson.fromJson(document.getString("activeGiveaways"), new TypeToken<CopyOnWriteArrayList<Long>>() {}.getType());
            List<Long> finishedGiveaways = this.gson.fromJson(document.getString("finishedGiveaways"), new TypeToken<CopyOnWriteArrayList<Long>>() {}.getType());
            List<UUID> scheduledGiveaways = this.gson.fromJson(document.getString("scheduledGiveaways"), new TypeToken<CopyOnWriteArrayList<UUID>>() {}.getType());
            List<Long> bannedUsers = this.gson.fromJson(document.getString("bannedUsers"), new TypeToken<CopyOnWriteArrayList<Long>>() {}.getType());
            Set<Long> managerRoles = this.gson.fromJson(document.getString("managerRoles"), new TypeToken<HashSet<Long>>() {}.getType());
            long premium = document.getLong("premium");
            String language = document.getString("language");
            return new Server(this.bot, id, activeGiveaways, finishedGiveaways, scheduledGiveaways, bannedUsers, managerRoles, presets, premium, language);
        };
    }

    @Override
    public Server create(Long id) {
        Server server = new Server(this.bot, id);
        this.save(server);
        return server;
    }

    public Map<String, EnumMap<Setting, String>> serializePresets(Map<String, Preset> unserialized) {
        Map<String, EnumMap<Setting, String>> serialized = Maps.newHashMap();
        for (Map.Entry<String, Preset> preset : unserialized.entrySet()) {
            serialized.put(preset.getKey(), preset.getValue().getSerializedSettings());
        }
        return serialized;
    }

    public Map<String, Preset> deserializePresets(long guildId, Map<String, Map<Setting, String>> serialized) {
        if (serialized.isEmpty()) {
            return new ConcurrentSkipListMap<>();
        }
        Guild guild = this.bot.getShardManager().getGuildById(guildId);
        Map<String, Preset> deserialized = new ConcurrentSkipListMap<>();
        for (Map.Entry<String, Map<Setting, String>> entry : serialized.entrySet()) {
            deserialized.put(entry.getKey(), new Preset(entry.getKey(), this.convertPresetValue(guild, entry.getValue())));
        }
        return deserialized;
    }

    public EnumMap<Setting, Object> convertPresetValue(Guild guild, Map<Setting, String> unconverted) {
        EnumMap<Setting, Object> enumMap = Maps.newEnumMap(Setting.class);
        for (Map.Entry<Setting, String> innerEntry : unconverted.entrySet()) {
            Object parsed = innerEntry.getKey().parseAny(innerEntry.getValue(), guild);
            if (parsed == null) {
                continue;
            }
            enumMap.put(innerEntry.getKey(), parsed);
        }
        return enumMap;
    }
}
