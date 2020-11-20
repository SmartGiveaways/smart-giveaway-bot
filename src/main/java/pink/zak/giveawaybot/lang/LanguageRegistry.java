package pink.zak.giveawaybot.lang;

import com.google.common.collect.Maps;
import com.timvisee.yamlwrapper.ConfigurationSection;
import com.timvisee.yamlwrapper.YamlConfiguration;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.awaitility.Awaitility;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.enums.Language;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.bot.SimpleBot;
import pink.zak.giveawaybot.service.text.Replace;
import pink.zak.giveawaybot.service.text.Replacer;
import pink.zak.giveawaybot.service.types.NumberUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LanguageRegistry {
    private final EnumMap<Language, LanguageContainer> languageMap = Maps.newEnumMap(Language.class);
    private Language defaultLanguage;
    private boolean isLoading;

    @SneakyThrows
    public void loadLanguages(SimpleBot bot) {
        this.defaultLanguage = Language.match(bot.getConfig("settings").string("default-language"));
        Files.walk(bot.getBasePath().resolve("lang"))
                .map(Path::toFile)
                .filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml"))
                .map(YamlConfiguration::loadFromFile)
                .forEach(config -> {
                    Language language = Language.match(config.getString("identifier"));
                    if (language == null) {
                        GiveawayBot.getLogger().error("Could not find identifier for language file {}", config.getName());
                        return;
                    }
                    this.languageMap.put(language, new LanguageContainer(language, config));
                });
        if (this.defaultLanguage == null || !this.languageMap.containsKey(this.defaultLanguage) || this.languageMap.get(this.defaultLanguage).getValues().size() < Language.values().length) {
            GiveawayBot.getLogger().error("The default language does not meet the requirements of 100% or could not be found.");
            System.exit(3);
        }
    }

    public void reloadLanguages(SimpleBot bot) {
        this.isLoading = true;
        this.languageMap.clear();
        this.loadLanguages(bot);
        this.isLoading = false;
    }

    public LangSub get(Language language, Text text) {
        return this.get(language, text, replacer -> replacer);
    }

    public LangSub get(Language language, Text text, Replace replace) {
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() -> !this.isLoading);
        if (this.isLoading) {
            return null;
        }
        LangSub retrieved = this.languageMap.get(language).get(text, replace);
        return retrieved == null ? this.fallback(text, replace) : retrieved;
    }

    public LangSub get(Server server, Text text) {
        return this.get(server.getLanguage(), text);
    }

    public LangSub get(Server server, Text text, Replace replace) {
        return this.get(server.getLanguage(), text, replace);
    }

    public LangSub fallback(Text text, Replace replace) {
        return this.get(this.defaultLanguage, text, replace);
    }

    public LangSub fallback(Text text) {
        return this.get(this.defaultLanguage, text);
    }

    private class LanguageContainer {
        private final Map<Text, LangSub> values = Maps.newHashMap();
        private final Language language;

        public LanguageContainer(Language language, YamlConfiguration config) {
            this.language = language;
            this.loadValues(config);
        }

        public void loadValues(YamlConfiguration config) {
            ConfigurationSection section = config.getSection("values");
            for (String key : section.getKeys()) {
                Text text = Text.match(key);
                if (text == null) {
                    GiveawayBot.getLogger().error("Could not match Text value from identifier {}", key);
                    return;
                }
                this.values.put(text, new LangSub(section.getString(key)));
            }
            int coverage = NumberUtils.getPercentage(this.values.size(), Text.values().length);
            if (coverage == 100) {
                GiveawayBot.getLogger().info("[Language] {} loaded {}/{} messages ({}% coverage)", this.language, this.values.size(), Text.values().length, coverage);
            } else if (coverage >= 90) {
                GiveawayBot.getLogger().warn("[Language] {} loaded {}/{} messages ({}% coverage)", this.language, this.values.size(), Text.values().length, coverage);
            } else {
                GiveawayBot.getLogger().error("[Language] {} loaded {}/{} messages ({}% coverage)", this.language, this.values.size(), Text.values().length, coverage);
            }
        }

        public LangSub get(Text text) {
            return this.values.get(text);
        }

        public LangSub get(Text text, Replace replace) {
            return this.values.containsKey(text) ? this.values.get(text).replace(replace) : null;
        }

        public Map<Text, LangSub> getValues() {
            return this.values;
        }

        public Language getLanguage() {
            return this.language;
        }
    }

    public class LangSub {
        private final String message;

        public LangSub(String message) {
            this.message = message;
        }

        public LangSub(String message, Replace replace) {
            this.message = replace.apply(new Replacer()).applyTo(message);
        }

        public void to(TextChannel channel) {
            channel.sendMessage(this.message).queue();
        }

        public void to(TextChannel channel, Consumer<Message> messageConsumer) {
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
