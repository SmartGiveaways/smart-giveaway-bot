package pink.zak.giveawaybot.discord.commands.discord.preset.subs.exports.utils;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.cache.ServerCache;
import pink.zak.giveawaybot.discord.enums.Setting;
import pink.zak.giveawaybot.discord.lang.LanguageRegistry;
import pink.zak.giveawaybot.discord.lang.enums.Text;
import pink.zak.giveawaybot.discord.models.Preset;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.service.BotConstants;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.cache.caches.Cache;
import pink.zak.giveawaybot.discord.service.cache.caches.WriteExpiringCache;
import pink.zak.giveawaybot.discord.service.tuple.ImmutablePair;
import pink.zak.giveawaybot.discord.storage.ServerStorage;
import pink.zak.giveawaybot.discord.threads.ThreadFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ImportCmdUtils extends ListenerAdapter {
    private final LanguageRegistry languageRegistry;
    private final ServerStorage serverStorage;
    private final ServerCache serverCache;

    private final Gson gson = new Gson();
    private final Cache<Long, JsonObject> confirmations;
    private final Cache<Long, Map<String, Preset>> serializedCache;

    public ImportCmdUtils(GiveawayBot bot) {
        this.languageRegistry = bot.getLanguageRegistry();
        this.serverStorage = bot.getServerStorage();
        this.serverCache = bot.getServerCache();

        this.confirmations = new WriteExpiringCache<>(bot, null, TimeUnit.MINUTES, 1, null, -1);
        this.serializedCache = new WriteExpiringCache<>(bot, null, TimeUnit.MINUTES, 1, null, -1);

        bot.registerListeners(this);
    }

    public void requestImport(Server server, Message message) {
        TextChannel channel = message.getTextChannel();
        if (message.getAttachments().isEmpty()) {
            this.languageRegistry.get(server, Text.PRESET_IMPORT_NO_ATTACHMENT).to(channel);
            return;
        }
        this.parseAttachment(message.getAttachments().get(0)).thenAccept(pair -> {
            switch (pair.getValue()) {
                case SUCCESS -> this.importOrConfirm(server, channel, message, pair.getKey());
                case MALFORMED_URL -> this.languageRegistry.get(server, Text.PRESET_IMPORT_INVALID_FILE, replacer -> replacer.set("exception", "MalformedUrlException")).to(channel);
                case INVALID_FILE_PARSING -> this.languageRegistry.get(server, Text.PRESET_IMPORT_INVALID_FILE, replacer -> replacer.set("exception", "JsonParseException")).to(channel);
                case INVALID_FILE_EXTENSION -> this.languageRegistry.get(server, Text.PRESET_IMPORT_INVALID_FILE, replacer -> replacer.set("exception", "InvalidFileExtension")).to(channel);
                case INVALID_FILE_MISSING_ELEMENTS -> this.languageRegistry.get(server, Text.PRESET_IMPORT_INVALID_FILE, replacer -> replacer.set("exception", "MissingPresetElements")).to(channel);
                default -> {}
            }
        }).exceptionally(ex -> {
            JdaBot.logger.error("Error parsing attachment", ex);
            return null;
        });
    }

    private void importOrConfirm(Server server, TextChannel channel, Message message, JsonObject jsonObject) {
        ImmutablePair<Set<String>, Map<String, Preset>> returnData = this.getAffectedPresets(server, message, jsonObject);
        Set<String> affected = returnData.getKey();
        Consumer<Message> messageAction = sent -> {
            this.confirmations.set(sent.getIdLong(), jsonObject);
            if (returnData.getValue() != null) {
                this.serializedCache.set(sent.getIdLong(), returnData.getValue());
            }
            sent.addReaction("\u2705").queue();
        };
        if (affected != null) {
            if (affected.isEmpty()) {
                this.languageRegistry.get(server, Text.PRESET_IMPORT_CONFIRM).to(channel, messageAction);
            } else if (affected.size() == 1) {
                this.languageRegistry.get(server, Text.PRESET_IMPORT_CONFIRM_OVERRIDE_SINGULAR, replacer -> replacer
                        .set("preset", affected.iterator().next())).to(channel, messageAction);
            } else {
                this.languageRegistry.get(server, Text.PRESET_IMPORT_CONFIRM_OVERRIDE_PLURAL, replacer -> replacer
                        .set("presets", String.join(", ", affected))).to(channel, messageAction);
            }
        }
    }

    private ImmutablePair<Set<String>, Map<String, Preset>> getAffectedPresets(Server server, Message message, JsonObject jsonObject) {
        long messageId = message.getIdLong();
        Map<String, Preset> cachedValues = this.serializedCache.get(messageId);
        if (cachedValues != null) {
            return ImmutablePair.of(this.getClashingPresetsMessage(server, cachedValues), cachedValues);
        }
        if (jsonObject.get("singular").getAsBoolean()) {
            String presetName = jsonObject.get("preset-name").getAsString();
            if (server.getPresets().containsKey(presetName)) {
                return ImmutablePair.of(Sets.newHashSet(presetName), null);
            }
            return ImmutablePair.of(Sets.newHashSet(), null);
        }
        try {
            Map<String, Preset> serialized = this.serverStorage.deserializePresets(server.getId(), this.gson.fromJson(jsonObject.get("preset-values").getAsString(), new TypeToken<HashMap<String, HashMap<Setting, String>>>(){}.getType()));
            return ImmutablePair.of(this.getClashingPresetsMessage(server, serialized), serialized);
        } catch (Exception ex) {
            this.languageRegistry.get(server, Text.PRESET_IMPORT_INVALID_FILE, replacer -> replacer.set("exception", ex.getClass().getSimpleName())).to(message.getTextChannel());
            return null;
        }
    }

    public Set<String> getClashingPresetsMessage(Server server, Map<String, Preset> serialized) {
        Set<String> clashing = Sets.newHashSet();
        for (Map.Entry<String, Preset> entry : server.getPresets().entrySet()) {
            if (serialized.containsKey(entry.getKey())) {
                clashing.add("`" + entry.getKey() + "`");
            }
        }
        return clashing;
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        long messageId = event.getMessageIdLong();
        if (event.getUser().isBot() || event.getReactionEmote().isEmote() || !this.confirmations.contains(messageId)) {
            return;
        }
        TextChannel channel = event.getChannel();
        this.serverCache.getAsync(event.getGuild().getIdLong(), ThreadFunction.GENERAL).thenAccept(server -> {
            if (!server.canMemberManage(event.getMember())) {
                return;
            }
            Map<String, Preset> toAdd = this.serializedCache.get(messageId);
            if (toAdd != null) {
                this.serializedCache.invalidate(messageId, false);
                this.confirmations.invalidate(messageId, false);
                Map<String, Preset> updatedPresets = server.getPresets();
                updatedPresets.putAll(toAdd);
                if (updatedPresets.size() > 10) {
                    this.languageRegistry.get(server, Text.PRESET_IMPORT_TOO_MANY).to(channel);
                    return;
                }
                if (toAdd.size() > 1) {
                    String presetList = toAdd.keySet().stream().map(str -> "`" + str + "`").collect(Collectors.joining(", "));
                    this.languageRegistry.get(server, Text.PRESET_IMPORTED_PLURAL, replacer -> replacer.set("presets", presetList)).to(channel);
                } else {
                    this.languageRegistry.get(server, Text.PRESET_IMPORTED_SINGULAR, replacer -> replacer.set("preset", toAdd.keySet().iterator().next())).to(channel);
                }
                server.setPresets(updatedPresets);
                return;
            }
            JsonObject json = this.confirmations.get(messageId);
            if (json != null) {
                this.confirmations.invalidate(messageId, false);
                if (server.getPresets().size() == 10) {
                    this.languageRegistry.get(server, Text.PRESET_IMPORT_TOO_MANY).to(channel);
                    return;
                }
                try {
                    String name = json.get("preset-name").getAsString();
                    EnumMap<Setting, Object> presetValues = this.serverStorage
                            .convertPresetValue(event.getGuild(), this.gson.fromJson(json.get("preset-values").getAsString(), new TypeToken<HashMap<Setting, String>>() {
                            }.getType()));
                    if (name == null || name.length() < 3 || presetValues == null || presetValues.isEmpty()) {
                        this.languageRegistry.get(server, Text.PRESET_IMPORT_INVALID_FILE, replacer -> replacer.set("exception", "MissingPresetElements")).to(channel);
                    } else {
                        Preset preset = new Preset(name, presetValues);
                        server.addPreset(preset);
                        this.languageRegistry.get(server, Text.PRESET_IMPORTED_SINGULAR, replacer -> replacer.set("preset", preset.getName())).to(channel);
                    }
                } catch (Exception ex) {
                    this.languageRegistry.get(server, Text.PRESET_IMPORT_INVALID_FILE, replacer -> replacer.set("exception", ex.getClass().getSimpleName())).to(channel);
                }
            }
        });
    }

    private CompletableFuture<ImmutablePair<JsonObject, ResponseCode>> parseAttachment(Message.Attachment attachment) {
        return CompletableFuture.supplyAsync(() -> {
            if (attachment.getFileExtension() == null || !attachment.getFileExtension().endsWith("json")) {
                return ImmutablePair.of(null, ResponseCode.INVALID_FILE_EXTENSION);
            }
            try (InputStream inputStream = new URL(attachment.getUrl()).openStream()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String json = reader.lines().collect(Collectors.joining(""));
                JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
                if (this.isMissingElements(jsonObject)) {
                    return ImmutablePair.of(jsonObject, ResponseCode.INVALID_FILE_MISSING_ELEMENTS);
                }
                return ImmutablePair.of(jsonObject, ResponseCode.SUCCESS);
            } catch (JsonParseException ex) {
                return ImmutablePair.of(null, ResponseCode.INVALID_FILE_PARSING);
            } catch (IOException ex) {
                return ImmutablePair.of(null, ResponseCode.MALFORMED_URL);
            }
        });
    }

    private boolean isMissingElements(JsonObject json) {
        if (!json.has("singular") || !json.has("preset-values")) {
            return true;
        }
        return json.get("singular").getAsBoolean() && !json.has("preset-name");
    }

    public enum ResponseCode {
        INVALID_FILE_EXTENSION,
        INVALID_FILE_PARSING,
        INVALID_FILE_MISSING_ELEMENTS,
        MALFORMED_URL,
        SUCCESS
    }
}
