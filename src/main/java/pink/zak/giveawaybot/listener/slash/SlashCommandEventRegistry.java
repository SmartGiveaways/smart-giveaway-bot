package pink.zak.giveawaybot.listener.slash;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pink.zak.giveawaybot.data.cache.ServerCache;
import pink.zak.giveawaybot.data.models.Server;

import java.util.Set;

public class SlashCommandEventRegistry extends ListenerAdapter {
    private final Set<SlashCommandListener> listeners = Sets.newHashSet();
    private ServerCache serverCache;

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        System.out.println("A");
        if (!event.isFromGuild() || this.serverCache == null) {
            System.out.println("B");
            return;
        }
        System.out.println("C");
        Server server = this.serverCache.get(event.getGuild().getIdLong());
        System.out.println("D");
        for (SlashCommandListener listener : this.listeners) {
            System.out.println("E");
            listener.onSlashCommand(server, event);
        }
    }

    public void addListener(SlashCommandListener listener) {
        System.out.println("Added listener for SlashCommandListener");
        this.listeners.add(listener);
    }

    public void removeListener(SlashCommandListener listener) {
        this.listeners.remove(listener);
    }

    public void setServerCache(ServerCache serverCache) {
        this.serverCache = serverCache;
    }
}
