package pink.zak.giveawaybot.defaults;

import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.models.Preset;

public class Defaults {
    private Preset defaultPreset;

    public Defaults() {
        this.setupDefaultPreset();
    }

    private void setupDefaultPreset() {
        this.defaultPreset = new Preset("default");
        this.defaultPreset.setSetting(Setting.REACT_TO_ENTER, true);
        this.defaultPreset.setSetting(Setting.MAX_ENTRIES, 1000);
    }

    public Preset getDefaultPreset() {
        return this.defaultPreset;
    }
}
