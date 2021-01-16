package pink.zak.test.giveawaybot.discord.service.types;

import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.service.types.BooleanUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BooleanUtilsTests {

    @Test
    void testIsBoolean() {
        assertTrue(BooleanUtils.isBoolean("false"));
        assertTrue(BooleanUtils.isBoolean("true"));
        assertTrue(BooleanUtils.isBoolean("yes"));
        assertTrue(BooleanUtils.isBoolean("no"));
        assertFalse(BooleanUtils.isBoolean("idkbro"));
    }

    @Test
    void testParseBoolean() {
        assertTrue(BooleanUtils.parseBoolean("true"));
        assertTrue(BooleanUtils.parseBoolean("yes"));
        assertFalse(BooleanUtils.parseBoolean("false"));
        assertFalse(BooleanUtils.parseBoolean("no"));
        assertFalse(BooleanUtils.parseBoolean("idkbro"));
        assertFalse(BooleanUtils.parseBoolean(""));
    }
}
