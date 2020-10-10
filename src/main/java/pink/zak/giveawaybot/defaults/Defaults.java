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
        this.defaultPreset.setSetting(Setting.ENABLE_REACT_TO_ENTER, true);
        this.defaultPreset.setSetting(Setting.ENABLE_MESSAGE_ENTRIES, true);
        this.defaultPreset.setSetting(Setting.ENTRIES_PER_MESSAGE, 1);
        this.defaultPreset.setSetting(Setting.ENABLE_INVITE_ENTRIES, true);
        this.defaultPreset.setSetting(Setting.ENTRIES_PER_INVITE, 250);
        this.defaultPreset.setSetting(Setting.MAX_ENTRIES, 1000);
        this.defaultPreset.setSetting(Setting.PING_WINNERS, true);
    }

    public Preset getDefaultPreset() {
        return this.defaultPreset;
    }
}
