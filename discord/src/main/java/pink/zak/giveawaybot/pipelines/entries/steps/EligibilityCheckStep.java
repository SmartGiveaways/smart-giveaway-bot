package pink.zak.giveawaybot.pipelines.entries.steps;

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
        // no, the int is NOT a redundant cast. Please go away i know i should recode a lot of settings.
        if (user.hasEntries(giveaway.messageId()) && user.entries(giveaway.messageId()).compareTo(BigInteger.valueOf((int) preset.getSetting(Setting.MAX_ENTRIES))) > -1) {
            return;
        }
        this.rewardStep.process(entryType, user, giveaway, preset);
    }

    private boolean isEntryEnabled(EntryType entryType, Preset preset) {
        return (boolean) switch (entryType) {
            case MESSAGES -> preset.getSetting(Setting.ENABLE_MESSAGE_ENTRIES);
            case REACTION -> preset.getSetting(Setting.ENABLE_REACT_TO_ENTER);
        };
    }
}
