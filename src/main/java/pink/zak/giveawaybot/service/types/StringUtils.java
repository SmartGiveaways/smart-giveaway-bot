package pink.zak.giveawaybot.service.types;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    public static boolean isNumerical(String input) {
        return org.apache.commons.lang3.StringUtils.isNumeric(input);
    }

    public static boolean isBoolean(String input) {
        return input.equalsIgnoreCase("false") || input.equalsIgnoreCase("true");
    }
}
