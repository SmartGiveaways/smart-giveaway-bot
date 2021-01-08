package pink.zak.giveawaybot.discord.lang;

import lombok.experimental.UtilityClass;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.lang.model.Language;
import pink.zak.giveawaybot.discord.service.types.NumberUtils;
import pink.zak.giveawaybot.discord.lang.enums.Text;

import java.util.Locale;

@UtilityClass
public class LanguageHelper {

    public static int getAndWarnCoverage(Language language) {
        int size = language.getValues().size();
        int coverage = NumberUtils.getPercentage(size, Text.values().length);
        if (coverage == 100) {
            GiveawayBot.logger().info("[Language] {} loaded {}/{} messages ({}% coverage)", language.getIdentifier(), size, Text.values().length, coverage);
        } else if (coverage >= 90) {
            GiveawayBot.logger().warn("[Language] {} loaded {}/{} messages ({}% coverage)", language.getIdentifier(), size, Text.values().length, coverage);
        } else {
            GiveawayBot.logger().error("[Language] {} loaded {}/{} messages ({}% coverage)", language.getIdentifier(), size, Text.values().length, coverage);
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
