package pink.zak.giveawaybot.data.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import pink.zak.giveawaybot.data.Defaults;
import pink.zak.giveawaybot.enums.Setting;

import java.util.Map;

public class Preset {
    private final String name;
    private final Map<Setting, Object> settings;

    public Preset(String name, Map<Setting, Object> settings) {
        this.name = name;
        this.settings = settings;
    }

    public Preset(String name) {
        this(name, Maps.newEnumMap(Setting.class));
    }

    public <T> T getSetting(Setting setting) {
        return this.hasSetting(setting) ? (T) this.settings.get(setting) : Defaults.defaultPreset.getSetting(setting);
    }

    public boolean hasSetting(Setting setting) {
        return this.settings.containsKey(setting);
    }

    public void setSetting(Setting setting, Object value) {
        this.settings.put(setting, value);
    }

    public String getName() {
        return this.name;
    }

    @JsonIgnore
    public Map<Setting, Object> getSettings() {
        return this.settings;
    }

    public Map<Setting, String> getSerializedSettings() {
        Map<Setting, String> serializedMap = Maps.newEnumMap(Setting.class);
        for (Map.Entry<Setting, Object> entry : this.settings.entrySet()) {
            serializedMap.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return serializedMap;
    }
}
