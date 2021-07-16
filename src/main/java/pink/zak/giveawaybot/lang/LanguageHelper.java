package pink.zak.giveawaybot.lang;

import lombok.experimental.UtilityClass;
import pink.zak.giveawaybot.lang.model.Language;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.types.NumberUtils;

import java.util.Locale;

@UtilityClass
public class LanguageHelper {

    public static int getAndWarnCoverage(Language language) {
        int size = language.getValues().size();
        int coverage = NumberUtils.getPercentage(size, Text.values().length);
        String message = "[Language] " + language.getIdentifier() + " loaded " + size + "/" + Text.values().length + " messages (" + coverage + "% coverage)";
        if (coverage == 100) {
            JdaBot.logger.info(message);
        } else if (coverage >= 90) {
            JdaBot.logger.warn(message);
        } else {
            JdaBot.logger.error(message);
        }
        return coverage;
    }

    public static String localeToId(Locale locale) {
        if (locale == Locale.UK) {
            return "en-uk";
        }
        return "en-uk";
    }
}
