package pink.zak.giveawaybot.entries.workers;

import com.google.common.collect.Maps;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.User;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;

import java.math.BigInteger;

public class EligibilityCheckStep {
    private final RewardStep rewardStep;

    public EligibilityCheckStep(GiveawayBot bot) {
        this.rewardStep = new RewardStep(bot.getMetricsLogger().getGenericBotMetrics());
    }

    public void process(EntryType entryType, User user, CurrentGiveaway giveaway, Preset preset) {
        if (!this.isEntryEnabled(entryType, preset)) { // No point doing any processing if the entry type is not enabled.
            return;
        }
        if (!giveaway.enteredUsers().contains(user.id())) {
            if (this.isEntryEnabled(EntryType.REACTION, preset)) {
                return;
            }
            user.entries().put(giveaway.messageId(), Maps.newEnumMap(EntryType.class));
            giveaway.enteredUsers().add(user.id());
        }
        if (!user.entries().containsKey(giveaway.messageId())) {
            user.entries().put(giveaway.messageId(), Maps.newEnumMap(EntryType.class));
        }
        if (user.hasEntries(giveaway.messageId()) && user.entries(giveaway.messageId()).compareTo(new BigInteger(String.valueOf(preset.getSetting(Setting.MAX_ENTRIES)))) > -1) {
            return;
        }
        this.rewardStep.process(entryType, user, giveaway, preset);
    }

    private boolean isEntryEnabled(EntryType entryType, Preset preset) {
        return switch (entryType) {
            case MESSAGES -> preset.getSetting(Setting.ENABLE_MESSAGE_ENTRIES);
            case REACTION -> preset.getSetting(Setting.ENABLE_REACT_TO_ENTER);
        };
    }
}
