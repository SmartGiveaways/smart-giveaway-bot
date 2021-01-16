package pink.zak.test.giveawaybot.discord.service.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.enums.EntryType;
import pink.zak.giveawaybot.discord.service.types.MapCreator;

import java.util.EnumMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MapCreatorTests {

    @Test
    void testCreate() {
        Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<EnumMap<EntryType, AtomicInteger>>() {}.getType(), new MapCreator<>(EntryType.class)).create();
        String testData = "{\"797162252788433016\":{\"MESSAGES\":7},\"797162205355180083\":{\"REACTION\":1, \"MESSAGES\":2},\"797162903589486642\":{\"MESSAGES\":12},\"797162420404748308\":{\"MESSAGES\":9},\"797162553083166773\":{\"MESSAGES\":18}}";
        ConcurrentMap<Long, EnumMap<EntryType, AtomicInteger>> entries = gson.fromJson(testData, new TypeToken<ConcurrentHashMap<Long, EnumMap<EntryType, AtomicInteger>>>() {}.getType());

        assertNotNull(entries);
        assertEquals(5, entries.size());
        assertEquals(1, entries.get(797162205355180083L).get(EntryType.REACTION).get());
        assertEquals(2, entries.get(797162205355180083L).get(EntryType.MESSAGES).get());
        assertEquals(18, entries.get(797162553083166773L).get(EntryType.MESSAGES).get());
    }
}
