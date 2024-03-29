package pink.zak.giveawaybot.pipelines.entries.steps;

import pink.zak.giveawaybot.data.models.Preset;
import pink.zak.giveawaybot.data.models.User;
import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.metrics.helpers.GenericMetrics;
import pink.zak.giveawaybot.pipelines.entries.EntryType;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RewardStep {
    private final AtomicInteger entryCount;

    public RewardStep(GenericMetrics metrics) {
        this.entryCount = metrics.getEntryCount();
    }

    public void process(EntryType entryType, User user, CurrentGiveaway giveaway, Preset preset) {
        Map<EntryType, AtomicInteger> entries = user.getEntries().get(giveaway.getMessageId());
        switch (entryType) {
            case MESSAGES -> this.add(entryType, entries, preset.getSetting(Setting.ENTRIES_PER_MESSAGE));
            case REACTION -> entries.put(entryType, new AtomicInteger(1));
            default -> {}
        }
    }

    private void add(EntryType entryType, Map<EntryType, AtomicInteger> entries, int amount) {
        this.entryCount.updateAndGet(current -> current + amount);
        if (entries.containsKey(entryType)) {
            entries.get(entryType).addAndGet(amount);
        } else {
            entries.put(entryType, new AtomicInteger(amount));
        }
    }
}
