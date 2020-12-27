package pink.zak.giveawaybot.service.command.global.argument;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.function.Predicate;

public class Argument<T> {
    private final ArgumentType<T> type;
    private final Set<String> aliases;
    private Predicate<String> tester;
    private String argumentName;

    public Argument(ArgumentType<T> type) {
        this.type = type;
        this.aliases = Sets.newHashSet();
    }

    public Argument(ArgumentType<T> type, Predicate<String> tester) {
        this.type = type;
        this.tester = tester;
        this.aliases = Sets.newHashSet();
    }

    /**
     * Creates an argument for use with a SimpleCommand.
     *
     * @param type         The clazz type of the argument, e.g a Player, OfflinePlayer, Integer or User.
     * @param argumentName The type of argument (used in help), e.g player or amount.
     */
    public Argument(ArgumentType<T> type, String argumentName) {
        this.type = type;
        this.argumentName = argumentName;
        this.aliases = Sets.newHashSet();
    }

    /**
     * Creates an argument for use with a SimpleCommand which has aliases.
     *
     * @param type         The clazz type of the argument, e.g a Player, OfflinePlayer, Integer or User.
     * @param argumentName The type of argument (used in help), e.g player or amount.
     * @param aliases      The alternatives (aliases) that can be used.
     */
    public Argument(ArgumentType<T> type, String argumentName, String... aliases) {
        this.type = type;
        this.argumentName = argumentName;
        this.aliases = Sets.newHashSet(aliases);
    }

    /**
     * Gets the type of the Argument.
     *
     * @return The ArgumentType of the class
     */
    public ArgumentType<T> getType() {
        return this.type;
    }

    /**
     * Gets the aliases, will return an empty hash set if there are none.
     *
     * @return The aliases of the command.
     */
    public Set<String> getAliases() {
        return this.aliases;
    }

    /**
     * Gets the tester of the argument
     */
    public Predicate<String> getTester() {
        return this.tester;
    }

    /**
     * Gets the argument of the class.
     *
     * @return The argument of the class, e.g "player" or "ban"
     */
    public String getArgumentName() {
        return this.argumentName;
    }
}