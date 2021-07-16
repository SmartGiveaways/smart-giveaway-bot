package pink.zak.test.giveawaybot.discord.service.types;

import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.service.types.NumberUtils;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumberUtilsTests {

    @Test
    void testIsNumerical() {
        assertTrue(NumberUtils.isNumerical("0346039468096845390684360943860934864309683409743857609438634098"));
        assertTrue(NumberUtils.isNumerical("0"));
        assertFalse(NumberUtils.isNumerical("45764567-234"));
        assertFalse(NumberUtils.isNumerical("-8"));
        assertFalse(NumberUtils.isNumerical("fizz"));
        assertFalse(NumberUtils.isNumerical(null));
    }

    @Test
    void testIsLikelyLong() {
        assertTrue(NumberUtils.isLikelyLong("584140343558275109"));
        assertFalse(NumberUtils.isLikelyLong("0346039468096845390684360943860934864309683409743857609438634098"));
        assertFalse(NumberUtils.isLikelyLong(""));
    }

    @Test
    void testIsLikelyInteger() {
        assertTrue(NumberUtils.isLikelyInteger("2147483647"));
        assertTrue(NumberUtils.isLikelyInteger("6"));
        assertFalse(NumberUtils.isLikelyInteger("12147483647"));
        assertFalse(NumberUtils.isLikelyInteger("idk"));
    }

    @Test
    void testParseInt() {
        assertEquals(10, NumberUtils.parseInt("10x", 10));
        assertEquals(-1, NumberUtils.parseInt("xwer", -1));
        assertEquals(2147483647, NumberUtils.parseInt("2147483647", -1));
        assertEquals(-20, NumberUtils.parseInt("-20", -1));
    }

    @Test
    void testParseDouble() {
        assertEquals(10, NumberUtils.parseDouble("10x", 10));
        assertEquals(-1, NumberUtils.parseDouble("xwer", -1));
        assertEquals(Double.MAX_VALUE, NumberUtils.parseDouble(String.valueOf(Double.MAX_VALUE), -1));
        assertEquals(-1, NumberUtils.parseDouble(Double.MAX_VALUE + "1" + Double.MAX_VALUE, -1));
        assertEquals(6.78, NumberUtils.parseDouble("6.78", -1));
        assertEquals(0, NumberUtils.parseDouble("0.0", -1));
    }

    @Test
    void testParseLong() {
        assertEquals(10, NumberUtils.parseLong("10x", 10));
        assertEquals(-1, NumberUtils.parseLong("xwer", -1));
        assertEquals(-1, NumberUtils.parseLong("9223372036854775809", -1));
        assertEquals(9223372036854775807L, NumberUtils.parseLong("9223372036854775807", -1));
        assertEquals(1076, NumberUtils.parseLong("1076", -1));
    }

    @Test
    void testGetRandomBigInteger() {
        BigInteger max = BigInteger.valueOf(500);
        BigInteger aboveMax = max.add(BigInteger.ONE);
        for (int i = 0; i < 10000; i++) {
            assertEquals(-1, NumberUtils.getRandomBigInteger(max).compareTo(aboveMax));
        }
    }

    @Test
    void testGetPercentage() {
        assertEquals(50, NumberUtils.getPercentage(2, 4));
        assertEquals(9, NumberUtils.getPercentage(238, 2630));
        assertEquals(0, NumberUtils.getPercentage(0, 1));
    }
}
