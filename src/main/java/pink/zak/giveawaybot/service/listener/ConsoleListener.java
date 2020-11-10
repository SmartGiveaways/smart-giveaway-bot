package pink.zak.giveawaybot.service.listener;

import pink.zak.giveawaybot.GiveawayBot;

import java.util.Scanner;

public class ConsoleListener implements Runnable {
    private final GiveawayBot bot;

    public ConsoleListener(GiveawayBot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            switch (input.toLowerCase()) {
                case "help":
                    GiveawayBot.getLogger().info("""
                            help -> Prints this command.
                            reload-lang -> Reloads language values.
                            stop -> Stops the bot and saves data.
                            """);
                case "reload-lang":
                    this.bot.getLanguageRegistry().reloadLanguages(this.bot);
                    break;
                case "stop":
                    if (this.bot.isInitialized()) {
                        System.exit(0);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
