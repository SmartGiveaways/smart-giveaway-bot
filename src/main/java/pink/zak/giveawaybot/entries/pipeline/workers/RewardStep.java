package pink.zak.giveawaybot.entries.pipeline.workers;

import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.models.Giveaway;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.User;

import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RewardStep {

    public void process(EntryType entryType, User user, Giveaway giveaway, Preset preset) {
        EnumMap<EntryType, AtomicInteger> entries = user.entries().get(giveaway.uuid());
        switch (entryType) {
            case INVITES -> this.add(entryType, entries, (int) preset.getSetting(Setting.ENTRIES_PER_INVITE));
            case MESSAGES -> this.add(entryType, entries, (int) preset.getSetting(Setting.ENTRIES_PER_MESSAGE));
            case REACTION -> entries.put(entryType, new AtomicInteger(1));
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
