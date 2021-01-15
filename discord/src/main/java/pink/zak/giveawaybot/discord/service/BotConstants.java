package pink.zak.giveawaybot.discord.service;

import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.text.Replace;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class BotConstants {
    public static final String VERSION = BotConstants.class.getPackage().getImplementationVersion();
    public static final Replace BASE_REPLACE = replacer -> replacer.set("version", VERSION);
    public static final String DEVICE_NAME = getInitDeviceName();

    public static final String BACK_ARROW = "\u2B05";
    public static final String FORWARD_ARROW = "\u27A1";

    private static String getInitDeviceName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            JdaBot.logger.error("Could not get local hostname", ex);
            return "UNKNOWN";
        }
    }
}
