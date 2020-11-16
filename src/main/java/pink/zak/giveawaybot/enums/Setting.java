package pink.zak.giveawaybot.enums;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Guild;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.service.types.ReactionContainer;
import pink.zak.giveawaybot.service.types.StringUtils;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public enum Setting {

    ENABLE_REACT_TO_ENTER(Text.PRESET_ENABLE_REACT_TO_ENTER_DESCRIPTION, Boolean::parseBoolean, StringUtils::isBoolean, "react-to-enter"),
    REACT_TO_ENTER_EMOJI(Text.PRESET_REACT_TO_ENTER_EMOJI_DESCRIPTION, ReactionContainer::fromUnknown, (str, guild) -> {
        return ReactionContainer.fromUnknown(str, guild) != null;
    }, "reaction-emote", "react-emote", "reaction-emoji", "react-emoji", "react-to-enter-emote", "reaction"),
    ENABLE_MESSAGE_ENTRIES(Text.PRESET_ENABLE_MESSAGE_ENTRIES_DESCRIPTION, Boolean::parseBoolean, StringUtils::isBoolean, "enable-message-entries", "use-message-entries"),
    ENTRIES_PER_MESSAGE(Text.PRESET_ENTRIES_PER_MESSAGE_DESCRIPTION, Text.PRESET_ENTRIES_PER_MESSAGE_LIMIT_MESSAGE, 3, Integer::parseInt, StringUtils::isNumerical, o -> ((Integer) o) <= 3, "entries-per-message", "message-entries"),
    MAX_ENTRIES(Text.PRESET_MAX_ENTRIES_DESCRIPTION, Text.PRESET_MAX_ENTRIES_LIMIT_MESSAGE, 10000, Integer::parseInt, StringUtils::isNumerical, o -> ((Integer) o) <= 10000, "max-entries"),
    PING_WINNERS(Text.PRESET_PING_WINNERS_DESCRIPTION, Boolean::parseBoolean, StringUtils::isBoolean, "ping-winners", "ping-giveaway-winners");

    private final Text description;
    private final Set<String> configNames;
    private final String primaryConfigName;
    // Nullable values
    private BiPredicate<String, Guild> guildInputChecker;
    private Function<String, Boolean> inputChecker;
    private Function<String, Object> parser;
    private BiFunction<String, Guild, Object> guildParser;

    private final Text limitMessage;
    private final Object maxValue;
    private final Predicate<Object> limitChecker;

    Setting(Text description, Text limitMessage, Object maxValue, BiFunction<String, Guild, Object> guildParser, BiPredicate<String, Guild> guildInputChecker, Predicate<Object> limitChecker, String... configNames) {
        this.description = description;
        this.limitMessage = limitMessage;
        this.maxValue = maxValue;
        this.guildParser = guildParser;
        this.guildInputChecker = guildInputChecker;
        this.limitChecker = limitChecker;
        this.primaryConfigName = configNames[0];
        this.configNames = Sets.newHashSet(configNames);
    }

    Setting(Text description, Text limitMessage, Object maxValue, Function<String, Object> parser, Function<String, Boolean> inputChecker, Predicate<Object> limitChecker, String... configNames) {
        this.description = description;
        this.limitMessage = limitMessage;
        this.maxValue = maxValue;
        this.parser = parser;
        this.inputChecker = inputChecker;
        this.limitChecker = limitChecker;
        this.primaryConfigName = configNames[0];
        this.configNames = Sets.newHashSet(configNames);
    }

    Setting(Text description, BiFunction<String, Guild, Object> guildParser, BiPredicate<String, Guild> guildInputChecker, String... configNames) {
        this(description, null, null, guildParser, guildInputChecker, null, configNames);
    }

    Setting(Text description, Function<String, Object> parser, Function<String, Boolean> inputChecker, String... configNames) {
        this(description, null, null, parser, inputChecker, null, configNames);
    }

    public Text getDescription() {
        return this.description;
    }

    public Text getLimitMessage() {
        return this.limitMessage;
    }

    public Object getMaxValue() {
        return this.maxValue;
    }

    public Object parse(String input) {
        return this.parser.apply(input);
    }

    public Object parse(String input, Guild guild) {
        return this.guildParser.apply(input, guild);
    }

    public Object parseAny(String input, Guild guild) {
        return this.parser == null ? this.parse(input, guild) : this.parse(input);
    }

    public boolean checkInput(String input, Guild guild) {
        return this.guildInputChecker.test(input, guild);
    }

    public boolean checkInput(String input) {
        return this.inputChecker.apply(input);
    }

    public boolean checkInputAny(String input, Guild guild) {
        return this.inputChecker == null ? this.checkInput(input, guild) : this.checkInput(input);
    }

    public boolean checkLimit(Object input) {
        return this.limitChecker == null || this.limitChecker.test(input);
    }

    public Set<String> getConfigNames() {
        return this.configNames;
    }

    public String getPrimaryConfigName() {
        return this.primaryConfigName;
    }

    public static Setting match(String input) {
        String lowerInput = input.toLowerCase();
        for (Setting setting : Setting.values()) {
            if (setting.toString().equalsIgnoreCase(input) || setting.getConfigNames().contains(lowerInput)) {
                return setting;
            }
        }
        return null;
    }
}
