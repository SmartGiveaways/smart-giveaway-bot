package pink.zak.giveawaybot.enums;

import com.google.common.collect.Sets;
import pink.zak.giveawaybot.service.types.StringUtils;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public enum Setting {

    ENABLE_REACT_TO_ENTER("React to message to enter", "", null, Boolean::parseBoolean, StringUtils::isBoolean, o -> true, "react-to-enter"),
    ENABLE_MESSAGE_ENTRIES("Send messages to gain entries", "", null, Boolean::parseBoolean, StringUtils::isBoolean, o -> true, "enable-message-entries", "use-message-entries"),
    ENTRIES_PER_MESSAGE("Entries per sent message", "The max entries per message is 3", 3, Integer::parseInt, StringUtils::isNumerical, o -> ((Integer) o) <= 3, "entries-per-message", "message-entries"),
    ENABLE_INVITE_ENTRIES("Invite users to gain entries", "", null, Boolean::parseBoolean, StringUtils::isBoolean, o -> true, "enable-invite-entries", "use-invite-entries"),
    ENTRIES_PER_INVITE("Entries per invited user", "The max entries per invite is 100", 500, Integer::parseInt, StringUtils::isNumerical, o -> ((Integer) o) <= 500, "entries-per-invite", "invite-entries"),
    MAX_ENTRIES("Maximum entries per user", "Your max entries per person must be below 10000", 10000, Integer::parseInt, StringUtils::isNumerical, o -> ((Integer) o) <= 10000, "max-entries"),
    PING_WINNERS("Whether winners will be ghost pinged", "", null, Boolean::parseBoolean, StringUtils::isBoolean, o -> true, "ping-winners", "ping-giveaway-winners");

    private final String description;
    private final String limitMessage;
    private final Object maxValue;
    private final Function<String, Object> parser;
    private final Predicate<String> formatChecker;
    private final Predicate<Object> limitChecker;
    private final Set<String> configNames;
    private final String primaryConfigName;

    Setting(String description, String limitMessage, Object maxValue, Function<String, Object> parser, Predicate<String> formatChecker, Predicate<Object> limitChecker, String... configNames) {
        this.description = description;
        this.limitMessage = limitMessage;
        this.maxValue = maxValue;
        this.parser = parser;
        this.formatChecker = formatChecker;
        this.limitChecker = limitChecker;
        this.primaryConfigName = configNames[0];
        this.configNames = Sets.newHashSet(configNames);
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

    public boolean checkFormat(String input) {
        return this.formatChecker.test(input);
    }

    public boolean checkLimit(Object input) {
        return this.limitChecker.test(input);
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
