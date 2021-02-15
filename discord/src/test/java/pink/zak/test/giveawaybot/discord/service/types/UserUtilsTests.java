package pink.zak.test.giveawaybot.discord.service.types;

import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.service.types.UserUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserUtilsTests {

    @Test
    void testParseIdInput() {
        assertEquals(340980635042578444L, UserUtils.parseIdInput("340980635042578444"));
        assertEquals(340980635042578444L, UserUtils.parseIdInput("<@340980635042578444>"));
        assertEquals(340980635042578444L, UserUtils.parseIdInput("<@!340980635042578444>"));
        // erroneous
        assertEquals(-1, UserUtils.parseIdInput("<@!aaaaaaaaaaaaaaaaaa>"));
        assertEquals(-1, UserUtils.parseIdInput("<@!340980623504257 8444>"));
    }
}
