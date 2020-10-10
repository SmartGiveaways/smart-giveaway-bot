package pink.zak.giveawaybot.entries.pipeline.workers;

import com.google.common.collect.Maps;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.models.Giveaway;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.User;

import java.math.BigInteger;

public class EligibilityCheckStep {
    private final RewardStep rewardStep;
    private final Preset defaultPreset;

    public EligibilityCheckStep(GiveawayBot bot) {
        this.rewardStep = new RewardStep(bot);
        this.defaultPreset = bot.getDefaults().getDefaultPreset();
    }

    public void process(EntryType entryType, User user, Giveaway giveaway, Preset preset) {
        if (!this.isEntryEnabled(entryType, preset)) { // No point doing any processing if the entry type is not enabled.
            // System.out.println("Entry is not enabled");
            return;
        }
        if ((!this.isEntryEnabled(EntryType.REACTION, preset) && !user.hasEntries(giveaway.uuid()) || (this.isEntryEnabled(EntryType.REACTION, preset) && entryType == EntryType.REACTION))) {
            user.entries().put(giveaway.uuid(), Maps.newEnumMap(EntryType.class));
            giveaway.enteredUsers().add(user.id());
        }
        // System.out.println("Going forward with entry type " + entryType);
        if (this.isEntryEnabled(EntryType.REACTION, preset)) {
            if (entryType != EntryType.REACTION && user.entries().get(giveaway.uuid()).get(EntryType.REACTION).get() < 1) {
                // System.out.println("Returning this shit");
                return;
            }
        }
        // System.out.println("Second part");
        if (user.hasEntries(giveaway.uuid()) && user.getEntries(giveaway.uuid()).compareTo(new BigInteger(String.valueOf(preset.getSetting(Setting.MAX_ENTRIES)))) > -1) {
            // System.out.println("Returning coz too many entries");
            return;
        }
        // System.out.println("Going to processing");
        this.rewardStep.process(entryType, user, giveaway, preset);
    }

    private boolean isEntryEnabled(EntryType entryType, Preset preset) {
        return switch (entryType) {
            case MESSAGES -> (boolean) (preset.hasSetting(Setting.ENABLE_MESSAGE_ENTRIES) ? preset.getSetting(Setting.ENABLE_MESSAGE_ENTRIES) : this.defaultPreset.getSetting(Setting.ENABLE_MESSAGE_ENTRIES));
            case INVITES -> (boolean) (preset.hasSetting(Setting.ENABLE_INVITE_ENTRIES) ? preset.getSetting(Setting.ENABLE_INVITE_ENTRIES) : this.defaultPreset.getSetting(Setting.ENABLE_INVITE_ENTRIES));
            case REACTION -> (boolean) (preset.hasSetting(Setting.ENABLE_REACT_TO_ENTER) ? preset.getSetting(Setting.ENABLE_REACT_TO_ENTER) : this.defaultPreset.getSetting(Setting.ENABLE_REACT_TO_ENTER));
        };
    }
}
