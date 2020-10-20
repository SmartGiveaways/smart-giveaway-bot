package pink.zak.giveawaybot.entries.pipeline.workers;

import com.google.common.collect.Maps;
import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.models.Giveaway;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.User;

import java.math.BigInteger;

public class EligibilityCheckStep {
    private final RewardStep rewardStep;

    public EligibilityCheckStep() {
        this.rewardStep = new RewardStep();
    }

    public void process(EntryType entryType, User user, Giveaway giveaway, Preset preset) {
        if (!this.isEntryEnabled(entryType, preset)) { // No point doing any processing if the entry type is not enabled.
            return;
        }
        // TODO I think there's a logic issue here that can cause a giveaway to be overridden.
        if ((!this.isEntryEnabled(EntryType.REACTION, preset) && !user.hasEntries(giveaway.uuid()) || (this.isEntryEnabled(EntryType.REACTION, preset) && entryType == EntryType.REACTION))) {
            user.entries().put(giveaway.uuid(), Maps.newEnumMap(EntryType.class));
            giveaway.enteredUsers().add(user.id());
        }
        if (this.isEntryEnabled(EntryType.REACTION, preset)) {
            if (entryType != EntryType.REACTION && (!user.entries().containsKey(giveaway.uuid()) || user.entries().get(giveaway.uuid()).get(EntryType.REACTION).get() < 1)) {
                return;
            }
        }
        if (user.hasEntries(giveaway.uuid()) && user.getEntries(giveaway.uuid()).compareTo(new BigInteger(String.valueOf(preset.getSetting(Setting.MAX_ENTRIES)))) > -1) {
            return;
        }
        giveaway.enteredUsers().add(user.id());
        this.rewardStep.process(entryType, user, giveaway, preset);
    }

    private boolean isEntryEnabled(EntryType entryType, Preset preset) {
        return (boolean) switch (entryType) {
            case MESSAGES -> preset.getSetting(Setting.ENABLE_MESSAGE_ENTRIES);
            case INVITES -> preset.getSetting(Setting.ENABLE_INVITE_ENTRIES);
            case REACTION -> preset.getSetting(Setting.ENABLE_REACT_TO_ENTER);
        };
    }
}
