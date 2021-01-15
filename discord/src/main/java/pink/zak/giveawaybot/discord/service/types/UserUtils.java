package pink.zak.giveawaybot.discord.service.types;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Member;

@UtilityClass
public class UserUtils {

    public static String getNameDiscrim(Member member) {
        return member.getEffectiveName() + "#" + member.getUser().getDiscriminator();
    }

    public long parseIdInput(String input) {
        if (input.contains(" ")) {
            return -1;
        }
        try {
            String toParse;
            if (input.length() == 18) {
                toParse = input;
            } else if (input.length() == 21) {
                toParse = input.substring(2, 20);
            } else if (input.length() == 22) {
                toParse = input.substring(3, 21);
            } else {
                toParse = "-1";
            }
            return Long.parseLong(toParse);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
