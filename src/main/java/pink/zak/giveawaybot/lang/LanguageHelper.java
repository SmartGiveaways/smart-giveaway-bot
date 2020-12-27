package pink.zak.giveawaybot.lang;

import lombok.experimental.UtilityClass;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.lang.model.Language;
import pink.zak.giveawaybot.service.types.NumberUtils;

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
}
