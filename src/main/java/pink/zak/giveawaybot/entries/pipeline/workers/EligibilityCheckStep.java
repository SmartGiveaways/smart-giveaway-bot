package pink.zak.giveawaybot.entries.pipeline.workers;

import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.models.Giveaway;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.User;

import java.math.BigInteger;

public class EligibilityCheckStep {
    private final RewardStep rewardStep = new RewardStep();

    public void process(EntryType entryType, User user, Giveaway giveaway, Preset preset) {
        if ((boolean) preset.getSetting(Setting.REACT_TO_ENTER) && entryType != EntryType.REACTION) {
            if (user.entries().get(giveaway.uuid()).get(EntryType.REACTION) < 1) {
                return;
            }
        }
        if (user.hasEntries(giveaway.uuid()) && user.getEntries(giveaway.uuid()).compareTo(new BigInteger(String.valueOf(preset.getSetting(Setting.MAX_ENTRIES)))) > -1) {
            return;
        }
        this.rewardStep.process(entryType, user, giveaway, preset);
    }
}
