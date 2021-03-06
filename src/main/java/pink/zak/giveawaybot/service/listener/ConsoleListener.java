package pink.zak.giveawaybot.service.listener;

import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.command.console.ConsoleCommandBase;

import java.util.Scanner;

public class ConsoleListener implements Runnable {
    private final ConsoleCommandBase commandBase;

    public ConsoleListener(JdaBot bot) {
        this.commandBase = bot.getConsoleCommandBase();
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            this.commandBase.onExecute(scanner.nextLine());
        }
    }
}
