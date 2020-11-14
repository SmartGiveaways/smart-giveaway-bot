package pink.zak.giveawaybot.entries.workers;

import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.User;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;

import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RewardStep {

    public void process(EntryType entryType, User user, CurrentGiveaway giveaway, Preset preset) {

        EnumMap<EntryType, AtomicInteger> entries = user.entries().get(giveaway.messageId());
        switch (entryType) {
            case MESSAGES -> this.add(entryType, entries, (int) preset.getSetting(Setting.ENTRIES_PER_MESSAGE));
            case REACTION -> entries.put(entryType, new AtomicInteger(1));
            default -> {}
        }
    }

    private void add(EntryType entryType, EnumMap<EntryType, AtomicInteger> entries, int amount) {
        if (entries.containsKey(entryType)) {
            entries.get(entryType).addAndGet(amount);
        } else {
            entries.put(entryType, new AtomicInteger(amount));
        }
    }
}
