package pink.zak.test.giveawaybot.discord.service.colour;

import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.colour.PaletteBuilder;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColourTests {

    @Test
    void testBuilder() {
        Palette basicPalette = new PaletteBuilder()
                .setPrimary(Color.PINK)
                .setSecondary(Color.ORANGE)
                .setSuccess(Color.GREEN)
                .setFailure(Color.RED)
                .build();

        assertEquals(Color.PINK, basicPalette.primary());
        assertEquals(Color.ORANGE, basicPalette.secondary());
        assertEquals(Color.GREEN, basicPalette.success());
        assertEquals(Color.RED, basicPalette.failure());

        Palette rgbPalette = new PaletteBuilder()
                .setPrimary(255, 105, 180)
                .setSecondary(255, 140, 0)
                .setSuccess(0, 255, 127)
                .setFailure(255, 69, 0)
                .build();

        assertEquals(-38476, rgbPalette.primary().getRGB());
        assertEquals(-29696, rgbPalette.secondary().getRGB());
        assertEquals(-16711809, rgbPalette.success().getRGB());
        assertEquals(-47872, rgbPalette.failure().getRGB());

        Palette hexPalette = new PaletteBuilder()
                .setPrimary("#FF69B4")
                .setSecondary("FF8C00")
                .setSuccess("#00FF7F")
                .setFailure("FF4500")
                .build();

        assertEquals(-38476, hexPalette.primary().getRGB());
        assertEquals(-29696, hexPalette.secondary().getRGB());
        assertEquals(-16711809, hexPalette.success().getRGB());
        assertEquals(-47872, hexPalette.failure().getRGB());
    }

    @Test
    void testErroneousBuilder() {
        PaletteBuilder paletteBuilderA = new PaletteBuilder();
        assertThrows(IllegalArgumentException.class, paletteBuilderA::build);
        PaletteBuilder paletteBuilderB = new PaletteBuilder().setPrimary(Color.RED);
        assertThrows(IllegalArgumentException.class, paletteBuilderB::build);
    }
}
