package pink.zak.giveawaybot.models.giveaway;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Message;

import java.util.Set;

public class CurrentGiveaway extends RichGiveaway {
    private final Set<Long> enteredUsers;

    public CurrentGiveaway(long messageId, long channelId, long serverId, long startTime, long endTime,
                           int winnerAmount, String presetName, String giveawayItem,
                           Set<Long> enteredUsers) {
        super(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem);
        this.enteredUsers = enteredUsers;
    }

    public CurrentGiveaway(long messageId, long channelId, long serverId, long startTime, long endTime, int winnerAmount, String presetName, String giveawayItem) {
        this(messageId, channelId, serverId, startTime, endTime, winnerAmount, presetName, giveawayItem, Sets.newHashSet());
    }

    public CurrentGiveaway(long messageId, long channelId, long serverId, long endTime, int winnerAmount, String presetName, String giveawayItem) {
        this(messageId, channelId, serverId, System.currentTimeMillis(), endTime, winnerAmount, presetName, giveawayItem);
    }

    public CurrentGiveaway(long endTime, Message message, int winnerAmount, String presetName, String giveawayItem) {
        this(message.getIdLong(), message.getChannel().getIdLong(), message.getGuild().getIdLong(), endTime, winnerAmount, presetName, giveawayItem);
    }

    @Override
    public long getTimeToExpiry() {
        return this.endTime - System.currentTimeMillis();
    }

    public boolean isActive() {
        return this.getTimeToExpiry() > 0;
    }

    public Set<Long> getEnteredUsers() {
        return this.enteredUsers;
    }
}
