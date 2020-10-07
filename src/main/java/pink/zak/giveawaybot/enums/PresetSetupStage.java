package pink.zak.giveawaybot.enums;

import java.util.function.Function;

public enum PresetSetupStage {

    // WIP, Might not happen. Ctrl click to find usage TODO
    FINISHED(null, "Finished setup. View your preset in >preset list or ", input -> null),
    SETTING_SETTING(PresetSetupStage.FINISHED, "Enter any other settings from this list that you would like you set a value for: ", Setting::match),
    SETTING_SETTING_VALUE(null, null, null),
    SETTING_MAX_ENTRIES(PresetSetupStage.SETTING_SETTING, "How many times can each member enter?", input -> Setting.MAX_ENTRIES),
    SETTING_REACT_TO_ENTER(PresetSetupStage.SETTING_MAX_ENTRIES, "Should users have to react to the giveaway message to enter?", input ->Setting.REACT_TO_ENTER),
    SETTING_NAME(PresetSetupStage.SETTING_REACT_TO_ENTER, "What should the preset be called?", input -> null);

    private final PresetSetupStage next;
    private final String message;
    private final Function<String, Setting> finder;

    PresetSetupStage(PresetSetupStage next, String message, Function<String, Setting> finder) {
        this.next = next;
        this.message = message;
        this.finder = finder;
    }

    public PresetSetupStage getNext() {
        return this.next;
    }

    public String getMessage() {
        return this.message;
    }

    public Function<String, Setting> getFinder() {
        return this.finder;
    }
}
