package pink.zak.giveawaybot.discord.service.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.timvisee.yamlwrapper.ConfigurationSection;
import com.timvisee.yamlwrapper.YamlConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class Config {
    private final File file;
    private final boolean reloadable;
    private YamlConfiguration configuration;
    private Map<String, Object> valueMap;

    public Config(File file, boolean reloadable) {
        this.file = file;
        this.reloadable = reloadable;
        this.reload();
    }

    public Config(Path basePath, UnaryOperator<Path> path, boolean reloadable) {
        this(path.apply(basePath).toFile(), reloadable);
    }

    public Map<String, Object> getValueMap() {
        return this.valueMap;
    }

    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    public boolean isReloadable() {
        return this.reloadable;
    }

    public String string(String key) {
        return (String) this.get(key);
    }

    public boolean bool(String key) {
        Object object = this.get(key);
        return object instanceof Boolean && (boolean) object;
    }

    public int integer(String key) {
        Object object = this.get(key);
        return object instanceof Number ? ((Number) object).intValue() : -1;
    }

    public Double doubl(String key) {
        Object object = this.get(key);
        return object instanceof Number ? ((Number) object).doubleValue() : -1;
    }

    @SuppressWarnings("unchecked")
    public List<String> stringList(String key) {
        Object object = this.get(key);
        return object instanceof List ? (List<String>) object : Lists.newArrayList();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(String key) {
        Object object = this.get(key);
        return object instanceof List ? (List<T>) object : Lists.newArrayList();
    }

    public List<String> keys(String key) {
        ConfigurationSection configurationSection = this.configuration.getSection(key);
        if (configurationSection == null) {
            return Lists.newArrayList();
        }
        return configurationSection.getKeys();
    }

    public Object get(String key) {
        return this.valueMap.getOrDefault(key, null);
    }

    public boolean has(String key) {
        return this.valueMap.containsKey(key);
    }

    public synchronized void load() {
        Map<String, Object> newValueMap = Maps.newHashMap();
        for (String key : this.configuration.getKeys()) {
            ConfigurationSection section = this.configuration.getConfigurationSection(key);
            if (!section.getKeys().isEmpty()) {
                newValueMap.putAll(this.resolveSection(key, section));
            } else {
                newValueMap.put(key, section.get());
            }
        }
        this.valueMap = newValueMap;
    }

    private Map<String, Object> resolveSection(String sectionKey, ConfigurationSection section) {
        Map<String, Object> values = Maps.newHashMap();
        for (String subKey : section.getKeys()) {
            String key = sectionKey + "." + subKey;
            ConfigurationSection retrieved = section.getSection(subKey);
            if (!retrieved.getKeys().isEmpty()) {
                values.putAll(this.resolveSection(key, retrieved));
            } else {
                values.put(key, retrieved.get());
            }
        }
        return values;
    }

    public void reload() {
        this.configuration = YamlConfiguration.loadFromFile(this.file);
        this.load();
    }
}