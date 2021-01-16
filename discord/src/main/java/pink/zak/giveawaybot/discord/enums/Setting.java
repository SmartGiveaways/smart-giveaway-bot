package pink.zak.giveawaybot.discord.enums;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Guild;
import pink.zak.giveawaybot.discord.lang.enums.Text;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.service.types.BooleanUtils;
import pink.zak.giveawaybot.discord.service.types.NumberUtils;
import pink.zak.giveawaybot.discord.service.types.ReactionContainer;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public enum Setting {

    ENABLE_REACT_TO_ENTER(Text.PRESET_ENABLE_REACT_TO_ENTER_DESCRIPTION, Boolean::parseBoolean, BooleanUtils::isBoolean, "react-to-enter"),
    REACT_TO_ENTER_EMOJI(Text.PRESET_REACT_TO_ENTER_EMOJI_DESCRIPTION, ReactionContainer::fromUnknown, (str, guild) ->
            ReactionContainer.fromUnknown(str, guild) != null
            , "reaction-emote", "react-emote", "reaction-emoji", "react-emoji", "react-to-enter-emote", "reaction"),
    ENABLE_MESSAGE_ENTRIES(Text.PRESET_ENABLE_MESSAGE_ENTRIES_DESCRIPTION, Boolean::parseBoolean, BooleanUtils::isBoolean, "enable-message-entries", "use-message-entries"),
    ENTRIES_PER_MESSAGE(Text.PRESET_ENTRIES_PER_MESSAGE_DESCRIPTION, Text.PRESET_ENTRIES_PER_MESSAGE_LIMIT_MESSAGE, 3, 9,
            str -> NumberUtils.parseInt(str, -1), str -> {
        int parsed = NumberUtils.parseInt(str, -1);
        return parsed > 0;
    }, (server, input) -> ((Integer) input) <= (server.isPremium() ? 9 : 3), "entries-per-message", "message-entries"),
    MAX_ENTRIES(Text.PRESET_MAX_ENTRIES_DESCRIPTION, Text.PRESET_MAX_ENTRIES_LIMIT_MESSAGE, 10000, 50000,
            str -> NumberUtils.parseInt(str, -1), str -> {
        int parsed = NumberUtils.parseInt(str, -1);
        return parsed > 0;
    }, (server, input) -> ((Integer) input) <= (server.isPremium() ? 50000 : 10000), "max-entries"),
    PING_WINNERS(Text.PRESET_PING_WINNERS_DESCRIPTION, Boolean::parseBoolean, BooleanUtils::isBoolean, "ping-winners", "ping-giveaway-winners"),
    WINNERS_MESSAGE(Text.PRESET_ENABLE_WINNERS_MESSAGE, Boolean::parseBoolean, BooleanUtils::isBoolean, "winner-message", "winners-message"),
    DM_WINNERS(Text.PRESET_ENABLE_DM_WINNERS, Boolean::parseBoolean, BooleanUtils::isBoolean, "dm-winners");

    private final Text description;
    private final Set<String> configNames;
    private final String primaryConfigName;
    private final Text limitMessage;
    private final Object maxValue;
    private final Object maxPremiumValue;
    private final BiPredicate<Server, Object> limitChecker;
    // Nullable values
    private BiPredicate<String, Guild> guildInputChecker;
    private Function<String, Boolean> inputChecker;
    private Function<String, Object> parser;
    private BiFunction<String, Guild, Object> guildParser;

    Setting(Text description, Text limitMessage, Object maxValue, Object maxPremiumValue, BiFunction<String, Guild, Object> guildParser, BiPredicate<String, Guild> guildInputChecker, BiPredicate<Server, Object> limitChecker, String... configNames) {
        this.description = description;
        this.limitMessage = limitMessage;
        this.maxValue = maxValue;
        this.maxPremiumValue = maxPremiumValue;
        this.guildParser = guildParser;
        this.guildInputChecker = guildInputChecker;
        this.limitChecker = limitChecker;
        this.primaryConfigName = configNames[0];
        this.configNames = Sets.newHashSet(configNames);
    }

    Setting(Text description, Text limitMessage, Object maxValue, Object maxPremiumValue, Function<String, Object> parser, Function<String, Boolean> inputChecker, BiPredicate<Server, Object> limitChecker, String... configNames) {
        this.description = description;
        this.limitMessage = limitMessage;
        this.maxValue = maxValue;
        this.maxPremiumValue = maxPremiumValue;
        this.parser = parser;
        this.inputChecker = inputChecker;
        this.limitChecker = limitChecker;
        this.primaryConfigName = configNames[0];
        this.configNames = Sets.newHashSet(configNames);
    }

    Setting(Text description, BiFunction<String, Guild, Object> guildParser, BiPredicate<String, Guild> guildInputChecker, String... configNames) {
        this(description, null, null, null, guildParser, guildInputChecker, null, configNames);
    }

    Setting(Text description, Function<String, Object> parser, Function<String, Boolean> inputChecker, String... configNames) {
        this(description, null, null, null, parser, inputChecker, null, configNames);
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

    public Text getDescription() {
        return this.description;
    }

    public Text getLimitMessage() {
        return this.limitMessage;
    }

    public Object getMaxValue() {
        return this.maxValue;
    }

    public Object getMaxPremiumValue() {
        return this.maxPremiumValue;
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

    public boolean checkLimit(Server server, Object input) {
        return this.limitChecker == null || this.limitChecker.test(server, input);
    }

    public Set<String> getConfigNames() {
        return this.configNames;
    }

    public String getPrimaryConfigName() {
        return this.primaryConfigName;
    }
}
