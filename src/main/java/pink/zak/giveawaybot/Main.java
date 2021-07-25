package pink.zak.giveawaybot;

import java.nio.file.Path;

public class Main {

    public static void main(String[] argsArray) {
        GiveawayBot bot = new GiveawayBot(path -> Path.of("C:\\Users\\shear\\Documents\\Projects\\SmartGiveaways\\smart-giveaway-bot\\build\\libs"));
        bot.load();
    }
}
