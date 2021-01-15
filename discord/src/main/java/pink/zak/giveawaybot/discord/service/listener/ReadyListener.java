package pink.zak.giveawaybot.discord.service.listener;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.bot.SimpleBot;

import java.util.concurrent.atomic.AtomicInteger;

public class ReadyListener extends ListenerAdapter {
    private final AtomicInteger countedShards = new AtomicInteger();
    private final SimpleBot bot;
    private int requiredShards;

    public ReadyListener(SimpleBot bot) {
        this.bot = bot;
    }

    public void setRequiredShards(int requiredShards) {
        JdaBot.logger.info("Set required shards to {}", requiredShards);
        this.requiredShards = requiredShards;
        if (this.countedShards.get() > this.requiredShards && this.bot.isInitialized()) {
            this.bot.onConnect();
        }
    }

    public void readyIfReady() {
        if (this.countedShards.get() > this.requiredShards) {
            this.bot.onConnect();
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent readyEvent) {
        if (this.bot.isConnected()) {
            return;
        }
        int shard = this.countedShards.incrementAndGet();
        JdaBot.logger.info("ReadyEvent called. Shard {}/{}", shard, this.requiredShards);
        if (shard < this.requiredShards) {
            return;
        }
        JdaBot.logger.info("All shards are up! Calling onConnect");
        this.bot.setConnected(true);
        if (this.bot.isInitialized()) {
            this.bot.onConnect();
        }
    }
}
