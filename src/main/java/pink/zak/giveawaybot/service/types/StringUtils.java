package pink.zak.giveawaybot.service.types;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    public static boolean isBoolean(String input) {
        return input.equalsIgnoreCase("false") || input.equalsIgnoreCase("true");
    }
}
