package pink.zak.giveawaybot.service.types;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BooleanUtils {

    public static boolean parseBoolean(String input) {
        String inputLower = input.toLowerCase();
        return !input.isEmpty() && (inputLower.equals("true") || inputLower.equals("yes"));
    }
}
