package pink.zak.giveawaybot.service.listener;

import com.sun.management.HotSpotDiagnosticMXBean;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ServerCache;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ConsoleListener implements Runnable {
    private final GiveawayBot bot;
    private final Logger logger;

    public ConsoleListener(GiveawayBot bot) {
        this.bot = bot;
        this.logger = GiveawayBot.getLogger();
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            String[] args = input.contains(" ") ? input.split(" ") : new String[]{input};
            String command = args[0];
            switch (command.toLowerCase()) {
                case "help":
                    this.logger.info("help -> Prints this command.");
                    this.logger.info("reload -> Reloads language values (built embeds require restart).");
                    this.logger.info("stop -> Stops the bot and saves data.");
                    this.logger.info("dump -> Creates a debug dump.");
                    break;
                case "reload":
                    this.bot.reload();
                    break;
                case "stop":
                    if (this.bot.isInitialized()) {
                        System.exit(0);
                    }
                    break;
                case "dump":
                    long startTime = System.currentTimeMillis();
                    String fileName = this.heapDump();
                    this.logger.info("Created your heap dump called " + fileName + " in " + (System.currentTimeMillis() - startTime) + "ms.");
                    break;
                case "unload-server":
                    if (args.length < 2) {
                        this.logger.info("You must specify a server ID.");
                        return;
                    }
                    ServerCache serverCache = this.bot.getServerCache();
                    long serverId = Long.parseLong(args[1]);
                    if (!serverCache.contains(serverId)) {
                        this.logger.info("The server {} is not cached", serverId);
                        return;
                    }
                    this.logger.info("Invalidating {} async", serverId);
                    serverCache.invalidate(serverId);
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
