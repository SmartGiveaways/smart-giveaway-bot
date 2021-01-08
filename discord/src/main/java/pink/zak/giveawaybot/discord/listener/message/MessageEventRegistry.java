package pink.zak.giveawaybot.discord.listener.message;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pink.zak.giveawaybot.discord.cache.ServerCache;
import pink.zak.giveawaybot.discord.models.Server;

import java.util.Set;

public class MessageEventRegistry extends ListenerAdapter {
    private final Set<GiveawayMessageListener> listeners = Sets.newHashSet();
    private ServerCache serverCache;

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || this.serverCache == null) {
            return;
        }
        Server server = this.serverCache.get(event.getGuild().getIdLong());
        for (GiveawayMessageListener listener : this.listeners) {
            listener.onExecute(server, event);
        }
    }

    public void addListener(GiveawayMessageListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(GiveawayMessageListener listener) {
        this.listeners.remove(listener);
    }

    public void setServerCache(ServerCache serverCache) {
        this.serverCache = serverCache;
    }
}
