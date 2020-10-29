package pink.zak.giveawaybot.enums;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Guild;
import pink.zak.giveawaybot.service.types.ReactionContainer;
import pink.zak.giveawaybot.service.types.StringUtils;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public enum Setting {

    ENABLE_REACT_TO_ENTER("React to message to enter", Boolean::parseBoolean, StringUtils::isBoolean, "react-to-enter"),
    REACT_TO_ENTER_EMOJI("The emoji to react with", ReactionContainer::fromUnknown, (str, guild) -> {
        return ReactionContainer.fromUnknown(str, guild) != null;
    }, "reaction-emote", "react-emote", "reaction-emoji", "react-emoji", "react-to-enter-emote", "reaction"),
    ENABLE_MESSAGE_ENTRIES("Send messages to gain entries", Boolean::parseBoolean, StringUtils::isBoolean, "enable-message-entries", "use-message-entries"),
    ENTRIES_PER_MESSAGE("Entries per sent message", "The max entries per message is 3", 3, Integer::parseInt, StringUtils::isNumerical, o -> ((Integer) o) <= 3, "entries-per-message", "message-entries"),
    ENABLE_INVITE_ENTRIES("Invite users to gain entries", Boolean::parseBoolean, StringUtils::isBoolean, "enable-invite-entries", "use-invite-entries"),
    ENTRIES_PER_INVITE("Entries per invited user", "The max entries per invite is 100", 500, Integer::parseInt, StringUtils::isNumerical, o -> ((Integer) o) <= 500, "entries-per-invite", "invite-entries"),
    MAX_ENTRIES("Maximum entries per user", "Your max entries per person must be below 10000", 10000, Integer::parseInt, StringUtils::isNumerical, o -> ((Integer) o) <= 10000, "max-entries"),
    PING_WINNERS("Whether winners will be ghost pinged", Boolean::parseBoolean, StringUtils::isBoolean, "ping-winners", "ping-giveaway-winners");

    private final String description;
    private final Set<String> configNames;
    private final String primaryConfigName;
    // Nullable values
    private BiPredicate<String, Guild> guildInputChecker;
    private Function<String, Boolean> inputChecker;
    private Function<String, Object> parser;
    private BiFunction<String, Guild, Object> guildParser;

    private final String limitMessage;
    private final Object maxValue;
    private final Predicate<Object> limitChecker;

    Setting(String description, String limitMessage, Object maxValue, BiFunction<String, Guild, Object> guildParser, BiPredicate<String, Guild> guildInputChecker, Predicate<Object> limitChecker, String... configNames) {
        this.description = description;
        this.limitMessage = limitMessage;
        this.maxValue = maxValue;
        this.guildParser = guildParser;
        this.guildInputChecker = guildInputChecker;
        this.limitChecker = limitChecker;
        this.primaryConfigName = configNames[0];
        this.configNames = Sets.newHashSet(configNames);
    }

    Setting(String description, String limitMessage, Object maxValue, Function<String, Object> parser, Function<String, Boolean> inputChecker, Predicate<Object> limitChecker, String... configNames) {
        this.description = description;
        this.limitMessage = limitMessage;
        this.maxValue = maxValue;
        this.parser = parser;
        this.inputChecker = inputChecker;
        this.limitChecker = limitChecker;
        this.primaryConfigName = configNames[0];
        this.configNames = Sets.newHashSet(configNames);
    }

    Setting(String description, BiFunction<String, Guild, Object> guildParser, BiPredicate<String, Guild> guildInputChecker, String... configNames) {
        this(description, null, null, guildParser, guildInputChecker, null, configNames);
    }

    Setting(String description, Function<String, Object> parser, Function<String, Boolean> inputChecker, String... configNames) {
        this(description, null, null, parser, inputChecker, null, configNames);
    }

    public String getDescription() {
        return this.description;
    }

    public String getLimitMessage() {
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
