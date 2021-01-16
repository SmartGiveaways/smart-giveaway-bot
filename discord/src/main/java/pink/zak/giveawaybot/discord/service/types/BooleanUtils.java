package pink.zak.giveawaybot.discord.service.types;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BooleanUtils {

    public static boolean parseBoolean(String input) {
        String inputLower = input.toLowerCase();
        return !input.isEmpty() && (inputLower.equals("true") || inputLower.equals("yes"));
    }

    public static boolean isBoolean(String input) {
        String inputLower = input.toLowerCase();
        return inputLower.equals("false") || inputLower.equals("true") || inputLower.equals("yes") || input.equals("no");
    }
}
