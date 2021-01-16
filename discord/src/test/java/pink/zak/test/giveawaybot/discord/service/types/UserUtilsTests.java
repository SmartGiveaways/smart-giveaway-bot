package pink.zak.test.giveawaybot.discord.service.types;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.service.types.UserUtils;
import pink.zak.test.giveawaybot.discord.TestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserUtilsTests {

    /**
     * Input cannot be erroneous
     */
    @Test
    void testGetNameDiscrim() {
        User user = new UserImpl(240721111174610945L, TestBase.JDA_MOCK)
                .setName("Zak")
                .setDiscriminator("6435");
        Member member = new MemberImpl(null, user);
        Member nickedMember = new MemberImpl(null, user)
                .setNickname("boop");

        assertEquals("Zak#6435", UserUtils.getNameDiscrim(member));
        assertEquals("boop#6435", UserUtils.getNameDiscrim(nickedMember));
    }

    @Test
    void testParseIdInput() {
        assertEquals(340980635042578444L, UserUtils.parseIdInput("340980635042578444"));
        assertEquals(340980635042578444L, UserUtils.parseIdInput("<@340980635042578444>"));
        assertEquals(340980635042578444L, UserUtils.parseIdInput("<@!340980635042578444>"));
        // erroneous
        assertEquals(-1, UserUtils.parseIdInput("<@!aaaaaaaaaaaaaaaaaa>"));
        assertEquals(-1, UserUtils.parseIdInput("<@!340980623504257 8444>"));
        assertEquals(-1, UserUtils.parseIdInput("<@!3409806235042578444>"));
    }
}
