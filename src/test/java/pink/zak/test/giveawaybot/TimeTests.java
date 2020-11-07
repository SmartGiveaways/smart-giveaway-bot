package pink.zak.test.giveawaybot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.service.time.Time;

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

        Assertions.assertEquals(1000, secondParseOne);
        Assertions.assertEquals(1000, secondParseTwo);
        Assertions.assertEquals(1000, secondParseThree);
        Assertions.assertEquals(4000, secondParseFour);
        Assertions.assertEquals(60000, minuteParseOne);
        Assertions.assertEquals(60000, minuteParseTwo);
        Assertions.assertEquals(60000, minuteParseThree);
        Assertions.assertEquals(240000, minuteParseFour);
        Assertions.assertEquals(3600000, hourParseOne);
        Assertions.assertEquals(3600000, hourParseTwo);
        Assertions.assertEquals(3600000, hourParseThree);
        Assertions.assertEquals(3600000, hourParseFour);
        Assertions.assertEquals(3600000, hourParseFive);
        Assertions.assertEquals(7200000, hourParseSix);
    }
}
