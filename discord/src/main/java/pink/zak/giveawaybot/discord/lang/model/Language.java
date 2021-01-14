package pink.zak.giveawaybot.discord.lang.model;

import pink.zak.giveawaybot.discord.lang.LanguageHelper;
import pink.zak.giveawaybot.discord.lang.LanguageRegistry;
import pink.zak.giveawaybot.discord.lang.enums.Text;

import java.util.Map;
import java.util.Set;

public class Language {
    private final String identifier;
    private final String name;
    private final String flag;
    private final Set<String> aliases;
    private final Map<Text, LanguageRegistry.LangSub> values;
    private final int coverage;

    public Language(String identifier, String name, String flag, Set<String> aliases, Map<Text, LanguageRegistry.LangSub> values) {
        this.identifier = identifier;
        this.name = name;
        this.flag = flag;
        this.aliases = aliases;
        this.values = values;
        this.coverage = LanguageHelper.getAndWarnCoverage(this);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getName() {
        return this.name;
    }

    public String getFlag() {
        return this.flag;
    }

    public Map<Text, LanguageRegistry.LangSub> getValues() {
        return this.values;
    }

    public LanguageRegistry.LangSub getValue(Text text) {
        return this.values.get(text);
    }

    public int getCoverage() {
        return this.coverage;
    }

    public boolean matches(String test) {
        String lowerTest = test.toLowerCase();
        return this.identifier.equals(lowerTest) || this.aliases.contains(lowerTest);
    }
}
