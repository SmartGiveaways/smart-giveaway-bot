package pink.zak.giveawaybot.discord.lang;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.timvisee.yamlwrapper.ConfigurationSection;
import com.timvisee.yamlwrapper.YamlConfiguration;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import pink.zak.giveawaybot.discord.lang.enums.Text;
import pink.zak.giveawaybot.discord.lang.model.Language;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.service.BotConstants;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.bot.SimpleBot;
import pink.zak.giveawaybot.discord.service.text.Replace;
import pink.zak.giveawaybot.discord.service.text.Replacer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LanguageRegistry {
    private final Map<String, Language> languageMap = Maps.newHashMap();
    private Language defaultLanguage;

    public void startLang(SimpleBot bot) {
        String defaultLanguageId = bot.getConfig("settings").string("default-language");
        this.loadLanguages(bot);
        this.defaultLanguage = this.languageMap.get(defaultLanguageId);
        if (this.defaultLanguage == null || !this.languageMap.containsKey(defaultLanguageId)) {
            JdaBot.logger.error("The default language could not be found.");
            System.exit(3);
        }
        Set<Text> presentTexts = this.defaultLanguage.getValues().keySet();
        if (presentTexts.size() < Text.values().length) {
            JdaBot.logger.error("The default language does not meet the 100% coverage requirement. These values are missing: {}",
                    Arrays.stream(Text.values()).filter(text -> !presentTexts.contains(text)).collect(Collectors.toSet()));
            System.exit(3);
        }
    }

    @SneakyThrows
    public Set<Language> loadLanguages(SimpleBot bot) {
        Set<Language> updatedLanguages = Sets.newHashSet();
        Stream<Path> pathStream = Files.walk(bot.getBasePath().resolve("lang"));
        pathStream.map(Path::toFile)
                .filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml"))
                .map(YamlConfiguration::loadFromFile)
                .forEach(config -> {
                    Language language = this.loadLanguage(config);
                    if (this.isLanguageUsable(language)) {
                        updatedLanguages.add(language);
                        this.languageMap.put(language.getIdentifier(), language);
                    }
                });
        pathStream.close();
        return updatedLanguages;
    }

    public Language loadLanguage(YamlConfiguration config) {
        String identifier = config.getString("identifier");
        String name = config.getString("name");
        String flag = config.getString("flag");
        Set<String> aliases = Sets.newHashSet((List<String>) config.getList("aliases"));
        Map<Text, LangSub> values = Maps.newHashMap();
        ConfigurationSection section = config.getSection("values");
        for (String key : section.getKeys()) {
            Text text = Text.match(key);
            if (text == null) {
                JdaBot.logger.error("Could not match Text value from identifier {} for language {}", key, identifier);
                continue;
            }
            values.put(text, new LangSub(section.getString(key), BotConstants.BASE_REPLACE));
        }
        return new Language(identifier, name, flag, aliases, values);
    }

    public boolean isLanguageUsable(Language language) {
        if (language.getCoverage() < 60) {
            JdaBot.logger.warn("Cannot user language {} as it has a coverage of {}%", language.getIdentifier(), language.getCoverage());
        }
        return language.getCoverage() > 60;
    }

    @SneakyThrows
    public void reloadLanguages(SimpleBot bot) {
        Set<Language> loadedLanguages = this.loadLanguages(bot);
        Set<String> loadedLanguageIds = loadedLanguages.stream().map(Language::getIdentifier).collect(Collectors.toSet());
        this.languageMap.keySet().stream().filter(existingLang -> !loadedLanguageIds.contains(existingLang)).forEach(existingLang -> {
            this.languageMap.remove(existingLang);
            JdaBot.logger.warn("Removed {} on language reload.", existingLang);
        });
    }

    public LangSub get(String language, Text text) {
        return this.get(language, text, null);
    }

    public LangSub get(String language, Text text, Replace replace) {
        LangSub retrieved = replace == null ? this.languageMap.get(language).getValue(text) : this.languageMap.get(language).getValue(text).replace(replace);
        return retrieved == null ? this.fallback(text, replace) : retrieved;
    }

    public LangSub get(Server server, Text text) {
        return this.get(server.getLanguage(), text);
    }

    public LangSub get(Server server, Text text, Replace replace) {
        return this.get(server.getLanguage(), text, replace);
    }

    public LangSub fallback(Text text, Replace replace) {
        return replace == null ? this.fallback(text) : this.defaultLanguage.getValue(text).replace(replace);
    }

    public LangSub fallback(Text text) {
        return this.defaultLanguage.getValue(text);
    }

    public Language getLanguage(String input) {
        String inputLower = input.toLowerCase();
        Language language = this.languageMap.get(inputLower);
        if (language != null) {
            return language;
        }
        for (Language loopLanguage : this.languageMap.values()) {
            if (loopLanguage.matches(inputLower)) {
                return loopLanguage;
            }
        }
        return null;
    }

    public Map<String, Language> languageMap() {
        return this.languageMap;
    }


    public static class LangSub {
        private final String message;

        public LangSub(String message, Replace replace) {
            this.message = replace.apply(new Replacer()).applyTo(message);
        }

        public void to(MessageChannel channel) {
            channel.sendMessage(this.message).queue();
        }

        public void to(MessageChannel channel, Consumer<Message> messageConsumer) {
            channel.sendMessage(this.message).queue(messageConsumer);
        }

        public String get() {
            return this.message;
        }

        public LangSub replace(Replace replace) {
            return new LangSub(this.message, replace);
        }

        @Override
        public String toString() {
            return this.message;
        }
    }
}
