package pink.zak.giveawaybot.lang;

import lombok.experimental.UtilityClass;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.lang.model.Language;
import pink.zak.giveawaybot.service.types.NumberUtils;

@UtilityClass
public class LanguageVerifierUtils {

    public static int getAndWarnCoverage(Language language) {
        int size = language.getValues().size();
        int coverage = NumberUtils.getPercentage(size, Text.values().length);
        if (coverage == 100) {
            GiveawayBot.getLogger().info("[Language] {} loaded {}/{} messages ({}% coverage)", language.getIdentifier(), size, Text.values().length, coverage);
        } else if (coverage >= 90) {
            GiveawayBot.getLogger().warn("[Language] {} loaded {}/{} messages ({}% coverage)", language.getIdentifier(), size, Text.values().length, coverage);
        } else {
            GiveawayBot.getLogger().error("[Language] {} loaded {}/{} messages ({}% coverage)", language.getIdentifier(), size, Text.values().length, coverage);
        }
        return coverage;
    }
}
