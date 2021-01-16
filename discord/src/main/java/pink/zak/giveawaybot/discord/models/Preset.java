package pink.zak.giveawaybot.discord.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import pink.zak.giveawaybot.discord.defaults.Defaults;
import pink.zak.giveawaybot.discord.enums.Setting;

import java.util.EnumMap;
import java.util.Map;

public class Preset {
    private final String name;
    private final EnumMap<Setting, Object> settings;

    public Preset(String name, EnumMap<Setting, Object> settings) {
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

    public EnumMap<Setting, String> getSerializedSettings() {
        EnumMap<Setting, String> serializedMap = Maps.newEnumMap(Setting.class);
        for (Map.Entry<Setting, Object> entry : this.settings.entrySet()) {
            serializedMap.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return serializedMap;
    }
}
