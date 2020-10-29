package pink.zak.giveawaybot.service.listener;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pink.zak.giveawaybot.service.bot.SimpleBot;

public class ReadyListener extends ListenerAdapter {
    private final SimpleBot bot;
    private boolean triggered;

    public ReadyListener(SimpleBot bot) {
        this.bot = bot;
    }

    @Override
    public void onReady(@NotNull ReadyEvent readyEvent) {
        if (!this.triggered) {
            this.triggered = true;
            this.bot.onConnect();
        }
    }
}
