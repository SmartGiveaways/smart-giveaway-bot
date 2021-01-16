package pink.zak.test.giveawaybot.discord.service.tuple;

import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.service.tuple.ImmutablePair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ImmutablePairTests {

    @Test
    void testAll() {
        ImmutablePair<Long, Long> pair = ImmutablePair.of(1000L, 2000L);
        assertNotNull(pair);
        assertEquals(1000, pair.getKey());
        assertEquals(2000, pair.getValue());
    }
}
