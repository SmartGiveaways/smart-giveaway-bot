package pink.zak.giveawaybot.models;

import com.google.common.collect.Maps;
import pink.zak.giveawaybot.defaults.Defaults;
import pink.zak.giveawaybot.enums.Setting;

import java.util.EnumMap;
import java.util.Map;

public record Preset(String name, EnumMap<Setting, Object> settings) {

    public Preset(String name) {
        this(name, Maps.newEnumMap(Setting.class));
    }

    public <T> T getSetting(Setting setting) { // TODO will generic types work? Try sometime
        return this.hasSetting(setting) ? (T) this.settings.get(setting) : Defaults.defaultPreset.getSetting(setting);
    }

    public boolean hasSetting(Setting setting) {
        return this.settings.containsKey(setting);
    }

    public void setSetting(Setting setting, Object value) {
        this.settings.put(setting, value);
    }

    public EnumMap<Setting, String> serialized() {
        EnumMap<Setting, String> serializedMap = Maps.newEnumMap(Setting.class);
        for (Map.Entry<Setting, Object> entry : this.settings.entrySet()) {
            serializedMap.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return serializedMap;
    }
}
