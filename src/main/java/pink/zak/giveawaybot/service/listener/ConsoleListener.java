package pink.zak.giveawaybot.service.listener;

import com.sun.management.HotSpotDiagnosticMXBean;
import lombok.SneakyThrows;
import pink.zak.giveawaybot.GiveawayBot;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
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
                    GiveawayBot.getLogger().info("help -> Prints this command.");
                    GiveawayBot.getLogger().info("reload-lang -> Reloads language values (built embeds require restart).");
                    GiveawayBot.getLogger().info("stop -> Stops the bot and saves data.");
                    GiveawayBot.getLogger().info("dump -> Creates a debug dump.");
                case "reload-lang":
                    this.bot.getLanguageRegistry().reloadLanguages(this.bot);
                    break;
                case "stop":
                    if (this.bot.isInitialized()) {
                        System.exit(0);
                    }
                    break;
                case "dump":
                    long startTime = System.currentTimeMillis();
                    String fileName = this.heapDump();
                    System.out.println("Created your heap dump called " + fileName + " in " + (System.currentTimeMillis() - startTime) + "ms.");
                    break;
                default:
                    break;
            }
        }
    }

    @SneakyThrows
    private String heapDump() {
        String fileName = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date(System.currentTimeMillis())) + " internal dump.hprof";
        ManagementFactory.newPlatformMXBeanProxy(ManagementFactory.getPlatformMBeanServer(),
                "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class)
                .dumpHeap(fileName, true);
        return fileName;
    }
}
