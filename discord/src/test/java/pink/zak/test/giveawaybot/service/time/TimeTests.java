package pink.zak.test.giveawaybot.service.time;

import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.service.time.Time;

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

        assert 1000 == secondParseOne;
        assert 1000 == secondParseTwo;
        assert 1000 == secondParseThree;
        assert 4000 == secondParseFour;
        assert 60000 == minuteParseOne;
        assert 60000 == minuteParseTwo;
        assert 60000 == minuteParseThree;
        assert 240000 == minuteParseFour;
        assert 3600000 == hourParseOne;
        assert 3600000 == hourParseTwo;
        assert 3600000 == hourParseThree;
        assert 3600000 == hourParseFour;
        assert 3600000 == hourParseFive;
        assert 7200000 == hourParseSix;
    }
}
