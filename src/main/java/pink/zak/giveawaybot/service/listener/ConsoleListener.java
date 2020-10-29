package pink.zak.giveawaybot.service.listener;

import pink.zak.giveawaybot.service.bot.SimpleBot;

import java.util.Scanner;

public class ConsoleListener implements Runnable {
    private final SimpleBot bot;

    public ConsoleListener(SimpleBot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("stop") && this.bot.isInitialized()) {
                System.exit(0);
            }
        }
    }
}
