package pink.zak.test.giveawaybot.discord.service.tuple;

import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.service.tuple.MutablePair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MutablePairTests {

    @Test
    void testAll() {
        MutablePair<Long, Long> pair = MutablePair.of(1000L, 2000L);
        assertNotNull(pair);
        assertEquals(1000, pair.getKey());
        assertEquals(2000, pair.getValue());
        pair.setKey(5000L);
        pair.setValue(10000L);
        assertEquals(5000, pair.getKey());
        assertEquals(10000, pair.getValue());
    }
}
