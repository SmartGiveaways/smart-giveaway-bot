package pink.zak.giveawaybot.discord.shutdown;

import pink.zak.giveawaybot.discord.GiveawayBot;

public class ShutdownHook extends Thread {
    private final GiveawayBot bot;

    public ShutdownHook(GiveawayBot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        this.bot.unload();
    }
}
