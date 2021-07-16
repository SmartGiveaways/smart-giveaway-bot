package pink.zak.test.giveawaybot.discord.service.time;

import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.service.time.Time;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeTests {

    /**
     * Input to {@link Time#formatAsDateTime(long)} is never erroneous
     */
    @Test
    void testFormatAsDateTime() {
        String formatOne = Time.formatAsDateTime(1610768008530L);
        String formatTwo = Time.formatAsDateTime(1600262008530L);
        String formatThree = Time.formatAsDateTime(1200262008530L);
        String formatFour = Time.formatAsDateTime(1800262008530L);

        assertEquals("2021-01-16 03:33:28", formatOne);
        assertEquals("2020-09-16 13:13:28", formatTwo);
        assertEquals("2008-01-13 22:06:48", formatThree);
        assertEquals("2027-01-18 08:46:48", formatFour);
    }

    /**
     * Input to {@link Time#format(long)} is never erroneous
     */
    @Test
    void testFormat() {
        String formatSecondsOne = Time.format(1000); // s
        String formatSecondsTwo = Time.format(8519);
        String formatMinutesOne = Time.format(60000); // min
        String formatMinutesTwo = Time.format(1261000);
        String formatMinutesThree = Time.format(487200);
        String formatHoursOne = Time.format(3600000); // hr
        String formatHoursTwo = Time.format(3660000);
        String formatHoursThree = Time.format(41511185);
        String formatHoursFour = Time.format(14729567);
        String formatDaysOne = Time.format(86400000); // 1d
        String formatDaysTwo = Time.format(96692136);
        String formatDaysThree = Time.format(263580000);
        String formatDaysFour = Time.format(347626914);
        String formatWeeksOne = Time.format(604800000); // 1w
        String formatWeeksTwo = Time.format(781200000);
        String formatWeeksThree = Time.format(1911600000);
        String formatMonthsOne = Time.format(2629743830L); // 1mo
        String formatMonthsTwo = Time.format(14531119150L);
        String formatMonthsThree = Time.format(21815550640L);

        assertEquals("1 second", formatSecondsOne);
        assertEquals("8 seconds", formatSecondsTwo);
        assertEquals("1 minute", formatMinutesOne);
        assertEquals("21 minutes 1 second", formatMinutesTwo);
        assertEquals("8 minutes 7 seconds", formatMinutesThree);
        assertEquals("1 hour", formatHoursOne);
        assertEquals("1 hour 1 minute", formatHoursTwo);
        assertEquals("11 hours 31 minutes", formatHoursThree);
        assertEquals("4 hours 5 minutes", formatHoursFour);
        assertEquals("1 day", formatDaysOne);
        assertEquals("1 day 2 hours", formatDaysTwo);
        assertEquals("3 days 1 hour", formatDaysThree);
        assertEquals("4 days", formatDaysFour);
        assertEquals("1 week", formatWeeksOne);
        assertEquals("1 week 2 days", formatWeeksTwo);
        assertEquals("3 weeks 1 day", formatWeeksThree);
        assertEquals("1 month", formatMonthsOne);
        assertEquals("5 months 2 weeks", formatMonthsTwo);
        assertEquals("8 months 1 week", formatMonthsThree);
    }

    @Test
    void testSimpleParse() {
        long secondParseOne = Time.parse("s");
        long secondParseTwo = Time.parse("1s");
        long secondParseThree = Time.parse("1sec");
        long secondParseFour = Time.parse("1second");
        long secondParseFive = Time.parse("4seconds");
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
        long dayParseOne = Time.parse("1d");
        long dayParseTwo = Time.parse("1day");
        long dayParseThree = Time.parse("7days");

        assertEquals(1000, secondParseOne);
        assertEquals(1000, secondParseTwo);
        assertEquals(1000, secondParseThree);
        assertEquals(1000, secondParseFour);
        assertEquals(4000, secondParseFive);
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
        assertEquals(86400000, dayParseOne);
        assertEquals(86400000, dayParseTwo);
        assertEquals(604800000, dayParseThree);
    }

    @Test
    void testNegativeSimpleParse() {
        long secondParseOne = Time.parse("-second");
        long secondParseTwo = Time.parse("-1s");
        long secondParseThree = Time.parse("-1sec");
        long secondParseFour = Time.parse("-1second");
        long secondParseFive = Time.parse("-4seconds");
        long minuteParseOne = Time.parse("-1m");
        long minuteParseTwo = Time.parse("-1min");
        long minuteParseThree = Time.parse("-1minute");
        long minuteParseFour = Time.parse("-4minutes");
        long hourParseOne = Time.parse("-1h");
        long hourParseTwo = Time.parse("-1hr");
        long hourParseThree = Time.parse("-1hrs");
        long hourParseFour = Time.parse("-1hour");
        long hourParseFive = Time.parse("-1hours");
        long hourParseSix = Time.parse("-2hours");
        long dayParseOne = Time.parse("-1d");
        long dayParseTwo = Time.parse("-1day");
        long dayParseThree = Time.parse("-7days");

        assertEquals(-1000, secondParseOne);
        assertEquals(-1000, secondParseTwo);
        assertEquals(-1000, secondParseThree);
        assertEquals(-1000, secondParseFour);
        assertEquals(-4000, secondParseFive);
        assertEquals(-60000, minuteParseOne);
        assertEquals(-60000, minuteParseTwo);
        assertEquals(-60000, minuteParseThree);
        assertEquals(-240000, minuteParseFour);
        assertEquals(-3600000, hourParseOne);
        assertEquals(-3600000, hourParseTwo);
        assertEquals(-3600000, hourParseThree);
        assertEquals(-3600000, hourParseFour);
        assertEquals(-3600000, hourParseFive);
        assertEquals(-7200000, hourParseSix);
        assertEquals(-86400000, dayParseOne);
        assertEquals(-86400000, dayParseTwo);
        assertEquals(-604800000, dayParseThree);
    }

    @Test
    void testCombinedParse() {
        long combinedParseOne = Time.parse("1day 6hours");
        long combinedParseTwo = Time.parse("1day 6hrs");
        long combinedParseThree = Time.parse("1month 1second");
        long combinedParseFour = Time.parse("3weeks 2days");

        assertEquals(108000000, combinedParseOne);
        assertEquals(108000000, combinedParseTwo);
        assertEquals(2628289000L, combinedParseThree);
        assertEquals(1987200000L, combinedParseFour);
    }

    @Test
    void testNegativeCombinedParse() {
        long combinedParseOne = Time.parse("-1day 6hours");
        long combinedParseTwo = Time.parse("1day -6hrs");
        long combinedParseThree = Time.parse("1month -1second");
        long combinedParseFour = Time.parse("3weeks 2days -1hr");

        assertEquals(-64800000, combinedParseOne);
        assertEquals(64800000, combinedParseTwo);
        assertEquals(2628287000L, combinedParseThree);
        assertEquals(1983600000L, combinedParseFour);
    }

    @Test
    void testErroneousParse() {
        long badParseOne = Time.parse("1beluga");
        long badParseTwo = Time.parse("x");
        long badParseThree = Time.parse("99999999999999999999999999999999999999999999days");

        assertEquals(-1, badParseOne);
        assertEquals(-1, badParseTwo);
        assertEquals(-1, badParseThree);
    }
}
