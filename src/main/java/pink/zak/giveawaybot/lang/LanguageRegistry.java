package pink.zak.giveawaybot.lang;

import com.google.common.collect.Maps;
import com.timvisee.yamlwrapper.ConfigurationSection;
import com.timvisee.yamlwrapper.YamlConfiguration;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.TextChannel;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.enums.Language;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.bot.SimpleBot;
import pink.zak.giveawaybot.service.text.Replace;
import pink.zak.giveawaybot.service.text.Replacer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public class LanguageRegistry {
    private final EnumMap<Language, LanguageContainer> languageMap = Maps.newEnumMap(Language.class);

    @SneakyThrows
    public void loadLanguages(SimpleBot bot) {
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
    }

    public LangSub get(Language language, Text text) {
        return this.languageMap.get(language).get(text);
    }

    public LangSub get(Language language, Text text, Replace replace) {
        return this.languageMap.get(language).get(text, replace);
    }

    public LangSub get(Server server, Text text) {
        return this.get(server.getLanguage(), text);
    }

    public LangSub get(Server server, Text text, Replace replace) {
        return this.get(server.getLanguage(), text, replace);
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
            GiveawayBot.getLogger().info("[Language] {} loaded {} messages", this.language, this.values.size());
        }

        public LangSub get(Text text) {
            return this.values.get(text);
        }

        public LangSub get(Text text, Replace replace) {
            return this.values.get(text).replace(replace);
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
