package pink.zak.giveawaybot;

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
