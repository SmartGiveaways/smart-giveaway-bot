package pink.zak.giveawaybot.service.listener;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.awaitility.Awaitility;
import org.jetbrains.annotations.NotNull;
import pink.zak.giveawaybot.service.bot.SimpleBot;

import java.util.concurrent.TimeUnit;

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
            this.bot.setConnected(true);
            Awaitility.await().atMost(5, TimeUnit.MINUTES).until(this.bot::isInitialized);
            this.bot.onConnect();
        }
    }
}
