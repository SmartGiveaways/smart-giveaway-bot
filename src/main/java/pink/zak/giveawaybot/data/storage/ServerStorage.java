package pink.zak.giveawaybot.data.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.bson.Document;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Preset;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoSerializer;
import pink.zak.giveawaybot.service.storage.mongo.MongoStorage;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServerStorage extends MongoStorage<Long, Server> {
    private final GiveawayBot bot;
    private final ShardManager shardManager;

    public ServerStorage(GiveawayBot bot) {
        super(bot.getThreadManager(), bot.getMongoConnectionFactory(), "server-settings", "_id");
        this.bot = bot;
        this.shardManager = bot.getShardManager();
    }

    @Override
    public MongoSerializer<Server> serializer() {
        return (server, document) -> {
            document.put("_id", server.getId());
            if (!server.getPresets().isEmpty())
                document.put("presets", this.createPresets(server));
            if (!server.getActiveGiveaways().isEmpty())
                document.put("activeGiveaways", server.getActiveGiveaways());
            if (!server.getFinishedGiveaways().isEmpty())
                document.put("finishedGiveaways", server.getFinishedGiveaways());
            if (!server.getScheduledGiveaways().isEmpty())
                document.put("scheduledGiveaways", server.getScheduledGiveaways());
            if (!server.getManagerRoles().isEmpty())
                document.put("managerRoles", server.getManagerRoles());
            if (!server.getBannedUsers().isEmpty())
                document.put("bannedUsers", server.getBannedUsers());
            document.put("premium", server.getPremiumExpiry());
            document.put("language", server.getLanguage());
            return document;
        };
    }

    @Override
    public MongoDeserializer<Server> deserializer() {
        return document -> {
            long id = document.getLong("_id");
            Map<String, Preset> presets = this.createPresets(id, document);
            List<Long> activeGiveaways = document.containsKey("activeGiveaways") ? Lists.newCopyOnWriteArrayList(document.getList("activeGiveaways", Long.class)) : Lists.newCopyOnWriteArrayList();
            List<Long> finishedGiveaways = document.containsKey("finishedGiveaways") ? Lists.newCopyOnWriteArrayList(document.getList("finishedGiveaways", Long.class)) : Lists.newCopyOnWriteArrayList();
            List<UUID> scheduledGiveaways = document.containsKey("scheduledGiveaways") ? Lists.newCopyOnWriteArrayList(document.getList("scheduledGiveaways", UUID.class)) : Lists.newCopyOnWriteArrayList();
            List<Long> bannedUsers = document.containsKey("bannedUsers") ? Lists.newCopyOnWriteArrayList(document.getList("bannedUsers", Long.class)) : Lists.newCopyOnWriteArrayList();
            Set<Long> managerRoles = document.containsKey("managerRoles") ? Sets.newConcurrentHashSet(document.getList("managerRoles", Long.class)) : Sets.newConcurrentHashSet();
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

    public Set<BasicDBObject> createPresets(Server server) {
        Set<BasicDBObject> documents = Sets.newHashSet();
        Map<String, Preset> presets = server.getPresets();

        for (Map.Entry<String, Preset> presetEntry : presets.entrySet()) {
            BasicDBObject presetObject = new BasicDBObject();
            presetObject.put("name", presetEntry.getKey());

            if (!presetEntry.getValue().getSettings().isEmpty()) {
                Set<BasicDBObject> settingObjects = Sets.newHashSet();
                for (Map.Entry<Setting, String> settingEntry : presetEntry.getValue().getSerializedSettings().entrySet()) {
                    BasicDBObject settingObject = new BasicDBObject();
                    settingObject.put("setting", settingEntry.getKey().toString());
                    settingObject.put("value", settingEntry.getValue());
                    settingObjects.add(settingObject);
                }
                presetObject.put("settings", settingObjects);
            }
            documents.add(presetObject);
        }
        return documents;
    }

    public Map<String, Preset> createPresets(long guildId, Document serverDocument) {
        Map<String, Preset> map = new ConcurrentHashMap<>();

        if (!serverDocument.containsKey("presets"))
            return map;

        Guild guild = this.shardManager.getGuildById(guildId);
        List<Document> presetDocuments = serverDocument.getList("presets", Document.class);

        for (Document presetDocument : presetDocuments) {
            String name = presetDocument.getString("name");

            if (presetDocument.containsKey("settings")) {
                List<Document> settingDocuments = presetDocument.getList("settings", Document.class);
                EnumMap<Setting, Object> settings = Maps.newEnumMap(Setting.class);

                for (Document settingDocument : settingDocuments) {
                    Setting setting = Setting.valueOf(settingDocument.getString("setting"));
                    Object value = this.convertPresetValue(guild, setting, settingDocument.getString("value"));

                    settings.put(setting, value);
                }
                map.put(name, new Preset(name, settings));
            } else {
                map.put(name, new Preset(name));
            }
        }
        return map;
    }

    public Object convertPresetValue(Guild guild, Setting setting, String unconverted) {
        return setting.parseAny(unconverted, guild);
    }
}
