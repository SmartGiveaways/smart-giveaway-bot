package pink.zak.test.giveawaybot.service.time;

import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.service.time.Time;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeTests {

    @Test
    void testParse() {
        long secondParseOne = Time.parse("1s");
        long secondParseTwo = Time.parse("1sec");
        long secondParseThree = Time.parse("1second");
        long secondParseFour = Time.parse("4seconds");
        long minuteParseOne = Time.parse("1m");
        long minuteParseTwo = Time.parse("1min");
        long minuteParseThree = Time.parse("1minute");
        long minuteParseFour = Time.parse("4minutes");
        long hourParseOne = Time.parse("1h");
        long hourParseTwo = Time.parse("1hr");
        long hourParseThree = Time.parse("1hrs");
        long hourParseFour = Time.parse("1hour");
        long hourParseFive = Time.parse("1hours");
        long hourParseSix = Time.parse("2hours");


        assertEquals(1000, secondParseOne);
        assertEquals(1000, secondParseTwo);
        assertEquals(1000, secondParseThree);
        assertEquals(4000, secondParseFour);
        assertEquals(60000, minuteParseOne);
        assertEquals(60000, minuteParseTwo);
        assertEquals(60000, minuteParseThree);
        assertEquals(240000, minuteParseFour);
        assertEquals(3600000, hourParseOne);
        assertEquals(3600000, hourParseTwo);
        assertEquals(3600000, hourParseThree);
        assertEquals(3600000, hourParseFour);
        assertEquals(3600000, hourParseFive);
        assertEquals(7200000, hourParseSix);
    }
}
