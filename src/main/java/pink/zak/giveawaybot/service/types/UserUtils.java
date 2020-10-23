package pink.zak.giveawaybot.service.types;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Member;

@UtilityClass
public class UserUtils {

    public static String getNameDiscrim(Member member) {
        return member.getEffectiveName() + "#" + member.getUser().getDiscriminator();
    }
}
