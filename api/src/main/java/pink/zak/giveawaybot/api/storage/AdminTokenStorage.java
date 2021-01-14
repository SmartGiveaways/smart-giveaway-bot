package pink.zak.giveawaybot.api.storage;

import pink.zak.giveawaybot.api.model.auth.AdminToken;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoDeserializer;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoSerializer;
import pink.zak.giveawaybot.discord.service.storage.mongo.MongoStorage;

public class AdminTokenStorage extends MongoStorage<String, AdminToken> {

    public AdminTokenStorage(GiveawayBot bot) {
        super(bot, "admin-tokens", "token");
    }

    @Override
    public MongoSerializer<AdminToken> serializer() {
        return (token, document) -> {
            document.put("token", token.getToken());
            document.put("issueTime", token.getIssueTime());
            return document;
        };
    }

    @Override
    public MongoDeserializer<AdminToken> deserializer() {
        return document -> {
            String token = document.getString("token");
            long issueTime = document.getLong("issueTime");
            return new AdminToken(token, issueTime);
        };
    }

    @Override
    public AdminToken create(String id) {
        return null;
    }
}
