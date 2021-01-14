package pink.zak.giveawaybot.argument;

import java.util.Arrays;

public enum ArgumentType {
    NO_API("noapi", "no-api");

    private final String[] names;

    ArgumentType(String... names) {
        this.names = names;
    }

    public String[] getNames() {
        return this.names;
    }

    public static ArgumentType findArg(String originalInput) {
        String input = originalInput;
        if (input.startsWith("-")) {
            input = input.substring(1);
        }
        for (ArgumentType argument : ArgumentType.values()) {
            if (Arrays.asList(argument.getNames()).contains(input)) {
                return argument;
            }
        }
        return null;
    }
}