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
            System.out.println("Not Enabled");
            return;
        }
        // TODO I think there's a logic issue here that can cause a giveaway to be overridden.
        if (!giveaway.enteredUsers().contains(user.id())) {
            if (this.isEntryEnabled(EntryType.REACTION, preset)) {
                System.out.println("No contain D: message id " + giveaway.messageId() + " : " + giveaway.uuid());
                return;
            }
            user.entries().put(giveaway.uuid(), Maps.newEnumMap(EntryType.class));
            giveaway.enteredUsers().add(user.id());
        }
        if (user.hasEntries(giveaway.uuid()) && user.entries(giveaway.uuid()).compareTo(new BigInteger(String.valueOf(preset.getSetting(Setting.MAX_ENTRIES)))) > -1) {
            System.out.println("Over max entries");
            return;
        }
        if (!user.hasEntries(giveaway.uuid())) {
            System.out.println("Doesnt have entries but invite is guuuud bro");
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
