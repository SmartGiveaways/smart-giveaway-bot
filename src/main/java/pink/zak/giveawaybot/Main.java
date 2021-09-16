package pink.zak.giveawaybot;

public class Main {

    public static void main(String[] argsArray) {
        GiveawayBot bot = new GiveawayBot(path -> path);
        bot.load();
    }
}
