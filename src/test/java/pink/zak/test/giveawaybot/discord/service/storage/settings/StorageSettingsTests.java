package pink.zak.test.giveawaybot.discord.service.storage.settings;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.service.storage.settings.StorageSettings;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageSettingsTests {
    private final StorageSettings storageSettings = new StorageSettings();

    @Test
    void testInitialization() {
        assertNotNull(this.storageSettings);
    }

    @Test
    void testAddressDetails() {
        this.storageSettings.setAddress("127.0.0.1");
        assertEquals("3306", this.storageSettings.getPort());
        assertEquals("127.0.0.1", this.storageSettings.getHost());
        this.storageSettings.setAddress("127.0.0.1:3000");
        assertEquals("127.0.0.1", this.storageSettings.getHost());
        assertEquals("3000", this.storageSettings.getPort());
    }

    @Test
    void testDatabaseDetails() {
        this.storageSettings.setDatabase("idk-database");
        this.storageSettings.setAuthDatabase("admin");
        this.storageSettings.setPrefix("sg-");
        assertEquals("idk-database", this.storageSettings.getDatabase());
        assertEquals("admin", this.storageSettings.getAuthDatabase());
        assertEquals("sg-", this.storageSettings.getPrefix());
        assertEquals("sg-users", this.storageSettings.addPrefixTo("users"));
    }

    @Test
    void testCredentials() {
        this.storageSettings.setUsername("root");
        this.storageSettings.setPassword("iAmVewwyGudPassword");
        assertEquals("root", this.storageSettings.getUsername());
        assertEquals("iAmVewwyGudPassword", this.storageSettings.getPassword());
    }

    @Test
    void testPoolSettings() {
        this.storageSettings.setMaximumPoolSize(100);
        this.storageSettings.setMaximumLifetime(18000);
        this.storageSettings.setConnectionTimeout(500);
        this.storageSettings.setMinimumIdle(100);
        assertEquals(100, this.storageSettings.getMaximumPoolSize());
        assertEquals(18000, this.storageSettings.getMaximumLifetime());
        assertEquals(500, this.storageSettings.getConnectionTimeout());
        assertEquals(100, this.storageSettings.getMinimumIdle());
    }

    @Test
    void testProperties() {
        Map<String, String> properties = Maps.newHashMap();
        properties.put("useSSL", "true");
        this.storageSettings.setProperties(properties);
        assertTrue(this.storageSettings.getProperties().containsKey("useSSL"));
        boolean useSsl = Boolean.parseBoolean(this.storageSettings.getProperties().get("useSSL"));
        assertTrue(useSsl);
    }
}
