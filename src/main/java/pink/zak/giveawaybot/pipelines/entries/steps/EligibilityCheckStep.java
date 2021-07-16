package pink.zak.giveawaybot.pipelines.entries.steps;

import com.google.common.collect.Maps;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Preset;
import pink.zak.giveawaybot.data.models.User;
import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.pipelines.entries.EntryType;

import java.math.BigInteger;

public class EligibilityCheckStep {
    private final RewardStep rewardStep;

    public EligibilityCheckStep(GiveawayBot bot) {
        this.rewardStep = new RewardStep(bot.getMetricsLogger().getGenericMetrics());
    }

    public void process(EntryType entryType, User user, CurrentGiveaway giveaway, Preset preset) {
        if (!this.isEntryEnabled(entryType, preset)) { // No point doing any processing if the entry type is not enabled.
            return;
        }
        long messageId = giveaway.getMessageId();
        if (!giveaway.getEnteredUsers().contains(user.getId())) {
            if (this.isEntryEnabled(EntryType.REACTION, preset)) {
                return;
            }
            user.getEntries().put(messageId, Maps.newEnumMap(EntryType.class));
            giveaway.getEnteredUsers().add(user.getId());
        }
        if (!user.getEntries().containsKey(messageId)) {
            user.getEntries().put(messageId, Maps.newEnumMap(EntryType.class));
        }
        // no, the int is NOT a redundant cast. Please go away i know i should recode a lot of settings.
        if (user.hasEntries(messageId) && user.getEntries(messageId).compareTo(BigInteger.valueOf((int) preset.getSetting(Setting.MAX_ENTRIES))) > -1) {
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
