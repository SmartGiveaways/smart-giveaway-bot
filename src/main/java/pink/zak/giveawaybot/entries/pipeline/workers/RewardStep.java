package pink.zak.giveawaybot.entries.pipeline.workers;

import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.models.Giveaway;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.User;

public class RewardStep {

    public void process(EntryType entryType, User user, Giveaway giveaway, Preset preset) {
        switch (entryType) {
            case INVITES:
            case MESSAGES:
            case REACTION:
        }
    }
}
