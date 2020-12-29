package pink.zak.giveawaybot.listener.reaction.pageable;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Set;

public class PageableReactionEventRegistry extends ListenerAdapter {
    private final Set<PageableReactionListener> listeners = Sets.newHashSet();

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot() || event.getReactionEmote().isEmote()) {
            return;
        }
        long messageId = event.getMessageIdLong();
        for (PageableReactionListener listener : this.listeners) {
            if (messageId == listener.getMessageId()) {
                String emoji = event.getReactionEmote().getEmoji();
                switch (emoji) {
                    case "\u27A1" -> listener.onReactionAdd(Page.NEXT, event);
                    case "\u2B05" -> listener.onReactionAdd(Page.PREVIOUS, event);
                    default -> {}
                }
            }
        }
    }

    public void addListener(PageableReactionListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(PageableReactionListener listener) {
        this.listeners.remove(listener);
    }
}
