package pink.zak.test.giveawaybot.discord.service;

import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.service.BotConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BotConstantTest {

    @Test
    void testDeviceName() {
        assertNotNull(BotConstants.DEVICE_NAME);
        assertNotEquals("UNKNOWN", BotConstants.DEVICE_NAME);
    }

    @Test
    void testArrows() {
        assertEquals("\u2B05", BotConstants.BACK_ARROW);
        assertEquals("\u27A1", BotConstants.FORWARD_ARROW);
    }
}
