package pink.zak.giveawaybot.enums;

import net.dv8tion.jda.api.entities.Guild;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.types.BooleanUtils;
import pink.zak.giveawaybot.service.types.NumberUtils;
import pink.zak.giveawaybot.service.types.ReactionContainer;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public enum Setting {

    ENABLE_REACT_TO_ENTER(Text.PRESET_ENABLE_REACT_TO_ENTER_DESCRIPTION, Boolean::parseBoolean, BooleanUtils::isBoolean, "Require Reaction To Enter"),
    REACT_TO_ENTER_EMOJI(Text.PRESET_REACT_TO_ENTER_EMOJI_DESCRIPTION, ReactionContainer::fromUnknown, (str, guild) ->
            ReactionContainer.fromUnknown(str, guild) != null
            , "Add Reaction Emoji"),
    ENABLE_MESSAGE_ENTRIES(Text.PRESET_ENABLE_MESSAGE_ENTRIES_DESCRIPTION, Boolean::parseBoolean, BooleanUtils::isBoolean, "Enable Message Entries"),
    ENTRIES_PER_MESSAGE(Text.PRESET_ENTRIES_PER_MESSAGE_DESCRIPTION, Text.PRESET_ENTRIES_PER_MESSAGE_LIMIT_MESSAGE, 3, 9,
            str -> NumberUtils.parseInt(str, -1), str -> {
        int parsed = NumberUtils.parseInt(str, -1);
        return parsed > 0;
    }, (server, input) -> ((Integer) input) <= (server.isPremium() ? 9 : 3), "Entries Per Message"),
    MAX_ENTRIES(Text.PRESET_MAX_ENTRIES_DESCRIPTION, Text.PRESET_MAX_ENTRIES_LIMIT_MESSAGE, 10000, 50000,
            str -> NumberUtils.parseInt(str, -1), str -> {
        int parsed = NumberUtils.parseInt(str, -1);
        return parsed > 0;
    }, (server, input) -> ((Integer) input) <= (server.isPremium() ? 50000 : 10000), "Maximum Entries (Per User)"),
    PING_WINNERS(Text.PRESET_PING_WINNERS_DESCRIPTION, Boolean::parseBoolean, BooleanUtils::isBoolean, "Ping Winners"),
    WINNERS_MESSAGE(Text.PRESET_ENABLE_WINNERS_MESSAGE, Boolean::parseBoolean, BooleanUtils::isBoolean, "Send Winners Message"),
    DM_WINNERS(Text.PRESET_ENABLE_DM_WINNERS, Boolean::parseBoolean, BooleanUtils::isBoolean, "DM Winners");

    private final Text description;
    private final String name;
    private final Text limitMessage;
    private final Object maxValue;
    private final Object maxPremiumValue;
    private final BiPredicate<Server, Object> limitChecker;
    // Nullable values
    private BiPredicate<String, Guild> guildInputChecker;
    private Function<String, Boolean> inputChecker;
    private Function<String, Object> parser;
    private BiFunction<String, Guild, Object> guildParser;

    Setting(Text description, Text limitMessage, Object maxValue, Object maxPremiumValue, BiFunction<String, Guild, Object> guildParser, BiPredicate<String, Guild> guildInputChecker, BiPredicate<Server, Object> limitChecker, String name) {
        this.description = description;
        this.limitMessage = limitMessage;
        this.maxValue = maxValue;
        this.maxPremiumValue = maxPremiumValue;
        this.guildParser = guildParser;
        this.guildInputChecker = guildInputChecker;
        this.limitChecker = limitChecker;
        this.name = name;
    }

    Setting(Text description, Text limitMessage, Object maxValue, Object maxPremiumValue, Function<String, Object> parser, Function<String, Boolean> inputChecker, BiPredicate<Server, Object> limitChecker, String name) {
        this.description = description;
        this.limitMessage = limitMessage;
        this.maxValue = maxValue;
        this.maxPremiumValue = maxPremiumValue;
        this.parser = parser;
        this.inputChecker = inputChecker;
        this.limitChecker = limitChecker;
        this.name = name;
    }

    Setting(Text description, BiFunction<String, Guild, Object> guildParser, BiPredicate<String, Guild> guildInputChecker, String name) {
        this(description, null, null, null, guildParser, guildInputChecker, null, name);
    }

    Setting(Text description, Function<String, Object> parser, Function<String, Boolean> inputChecker, String name) {
        this(description, null, null, null, parser, inputChecker, null, name);
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

    public String getName() {
        return this.name;
    }
}
