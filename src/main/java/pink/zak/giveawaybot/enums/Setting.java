package pink.zak.giveawaybot.enums;

import com.google.common.collect.Sets;
import pink.zak.giveawaybot.service.types.StringUtils;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public enum Setting {

    ENABLE_REACT_TO_ENTER("React to message to enter", Boolean::parseBoolean, StringUtils::isBoolean, "react-to-enter"),
    ENABLE_MESSAGE_ENTRIES("Send messages to gain entries", Boolean::parseBoolean, StringUtils::isBoolean, "enable-message-entries", "use-message-entries"),
    ENTRIES_PER_MESSAGE("Entries per sent message", "The max entries per message is 3", 3, Integer::parseInt, StringUtils::isNumerical, o -> ((Integer) o) <= 3, "entries-per-message", "message-entries"),
    ENABLE_INVITE_ENTRIES("Invite users to gain entries", Boolean::parseBoolean, StringUtils::isBoolean, "enable-invite-entries", "use-invite-entries"),
    ENTRIES_PER_INVITE("Entries per invited user", "The max entries per invite is 100", 500, Integer::parseInt, StringUtils::isNumerical, o -> ((Integer) o) <= 500, "entries-per-invite", "invite-entries"),
    MAX_ENTRIES("Maximum entries per user", "Your max entries per person must be below 10000", 10000, Integer::parseInt, StringUtils::isNumerical, o -> ((Integer) o) <= 10000, "max-entries"),
    PING_WINNERS("Whether winners will be ghost pinged", Boolean::parseBoolean, StringUtils::isBoolean, "ping-winners", "ping-giveaway-winners");

    private final String description;
    private final Function<String, Object> parser;
    private final Predicate<String> inputChecker;
    private final Set<String> configNames;
    private final String primaryConfigName;
    // Nullable values
    private final String limitMessage;
    private final Object maxValue;
    private final Predicate<Object> limitChecker;

    Setting(String description, String limitMessage, Object maxValue, Function<String, Object> parser, Predicate<String> inputChecker, Predicate<Object> limitChecker, String... configNames) {
        this.description = description;
        this.limitMessage = limitMessage;
        this.maxValue = maxValue;
        this.parser = parser;
        this.inputChecker = inputChecker;
        this.limitChecker = limitChecker;
        this.primaryConfigName = configNames[0];
        this.configNames = Sets.newHashSet(configNames);
    }

    Setting(String description, Function<String, Object> parser, Predicate<String> inputChecker, String... configNames) {
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

    public boolean checkInput(String input) {
        return this.inputChecker.test(input);
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
