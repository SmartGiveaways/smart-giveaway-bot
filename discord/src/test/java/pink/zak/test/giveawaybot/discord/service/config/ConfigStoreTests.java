package pink.zak.test.giveawaybot.discord.service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.service.config.Config;
import pink.zak.giveawaybot.discord.service.config.ConfigStore;
import pink.zak.test.giveawaybot.discord.TestBase;
import pink.zak.test.giveawaybot.discord.fakes.FakeConfigStore;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigStoreTests {
    private ConfigStore configStore;

    @Test
    @BeforeEach
    void testCreation() {
        this.configStore = new FakeConfigStore(TestBase.SIMPLE_BOT.getBasePath());
        this.configStore.config("config-1", (path, s) -> path.resolve("spam").resolve(s), false);
        this.configStore.config("config-2", (path, s) -> path.resolve("spam").resolve(s), true);
        this.configStore.config("config-3", (path, s) -> path.resolve("spam").resolve(s), true);
    }

    @Test
    void testReload() {
        Config config1 = this.configStore.getConfig("config-1");
        Config config2 = this.configStore.getConfig("config-2");
        config1.getValueMap().put("A", 1);
        config2.getValueMap().put("B", 2);
        this.configStore.reloadReloadableConfigs();
        assertTrue(config1.has("A"));
        assertFalse(config2.has("B"));
    }

    @Test
    void testForceReload() {
        Config config = this.configStore.getConfig("config-1");
        config.getValueMap().put("A", 1);
        this.configStore.forceReload("config-1");
        assertFalse(config.has("A"));
        assertDoesNotThrow(() -> this.configStore.forceReload("config-that-doesnt-exist"));
    }

    @Test
    void testCommon() {
        assertDoesNotThrow(() -> this.configStore.common("idk", "config-1", config -> config.string("test-string")));
        assertEquals("hello", this.configStore.commons().get("idk"));
    }
}
