package pink.zak.giveawaybot.discord.service;

import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.text.Replace;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class BotConstants {
    private static final String version = BotConstants.class.getPackage().getImplementationVersion();
    private static final Replace baseReplace = replacer -> replacer.set("version", version);
    private static final String deviceName = getInitDeviceName();

    private static final String backArrow = "\u2B05";
    private static final String forwardArrow = "\u27A1";

    private static String getInitDeviceName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            JdaBot.logger.error("Could not get local hostname", ex);
            return "UNKNOWN";
        }
    }

    public static String getDeviceName() {
        return deviceName;
    }

    public static String getVersion() {
        return version;
    }

    public static Replace getBaseReplace() {
        return baseReplace;
    }

    public static String getBackArrow() {
        return backArrow;
    }

    public static String getForwardArrow() {
        return forwardArrow;
    }
}
