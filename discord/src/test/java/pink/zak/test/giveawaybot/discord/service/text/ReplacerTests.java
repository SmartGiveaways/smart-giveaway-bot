package pink.zak.test.giveawaybot.discord.service.text;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.service.text.Replace;
import pink.zak.giveawaybot.discord.service.text.Replacer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReplacerTests {

    @Test
    void toTest() {
        Replace replace = replacer -> replacer.set("time", "18:50").set("date", "16/01/2021");
        String output = Replacer.to("The time is %time% and the date is %date%", replace);
        assertEquals("The time is 18:50 and the date is 16/01/2021", output);
    }

    @Test
    void ofTest() {
        Map<String, String> values = Maps.newHashMap();
        values.put("time", "18:50");
        values.put("date", "16/01/2021");

        String output = Replacer.of(values).applyTo("The time is %time% and the date is %date%");
        assertEquals("The time is 18:50 and the date is 16/01/2021", output);
    }

    @Test
    void addReplacesTest() {
        Replace replace1 = replacer -> replacer.set("time", "18:50");
        Replace replace2 = replacer -> replacer.set("date", "16/01/2021");
        String output = new Replacer().addReplaces(replace1, replace2).applyTo("The time is %time% and the date is %date%");
        assertEquals("The time is 18:50 and the date is 16/01/2021", output);
    }
}
