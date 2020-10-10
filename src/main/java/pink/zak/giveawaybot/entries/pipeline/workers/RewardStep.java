package pink.zak.giveawaybot.entries.pipeline.workers;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.models.Giveaway;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.User;

import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RewardStep {
    private final Preset defaultPreset;

    public RewardStep(GiveawayBot bot) {
        this.defaultPreset = bot.getDefaults().getDefaultPreset();
    }

    public void process(EntryType entryType, User user, Giveaway giveaway, Preset preset) {
        EnumMap<EntryType, AtomicInteger> entries = user.entries().get(giveaway.uuid());
        switch (entryType) {
            case INVITES -> this.add(entryType, entries, this.getOrDefault(Setting.ENTRIES_PER_INVITE, preset));
            case MESSAGES -> this.add(entryType, entries, this.getOrDefault(Setting.ENTRIES_PER_MESSAGE, preset));
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

    private int getOrDefault(Setting setting, Preset preset) {
        return (int) (preset.hasSetting(setting) ?  preset.getSetting(setting) : this.defaultPreset.getSetting(setting));
    }
}
