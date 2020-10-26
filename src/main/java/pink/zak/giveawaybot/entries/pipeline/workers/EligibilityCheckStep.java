package pink.zak.giveawaybot.entries.pipeline.workers;

import com.google.common.collect.Maps;
import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.User;

import java.math.BigInteger;

public class EligibilityCheckStep {
    private final RewardStep rewardStep;

    public EligibilityCheckStep() {
        this.rewardStep = new RewardStep();
    }

    public void process(EntryType entryType, User user, CurrentGiveaway giveaway, Preset preset) {
        if (!this.isEntryEnabled(entryType, preset)) { // No point doing any processing if the entry type is not enabled.
            return;
        }
        // TODO I think there's a logic issue here that can cause a giveaway to be overridden.
        if (!giveaway.enteredUsers().contains(user.id())) {
            if (this.isEntryEnabled(EntryType.REACTION, preset)) {
                return;
            }
            user.entries().put(giveaway.messageId(), Maps.newEnumMap(EntryType.class));
            giveaway.enteredUsers().add(user.id());
        }
        if (user.hasEntries(giveaway.messageId()) && user.entries(giveaway.messageId()).compareTo(new BigInteger(String.valueOf(preset.getSetting(Setting.MAX_ENTRIES)))) > -1) {
            return;
        }
        if (!user.hasEntries(giveaway.messageId())) {
        }
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
