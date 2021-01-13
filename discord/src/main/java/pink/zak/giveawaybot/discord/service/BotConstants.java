package pink.zak.giveawaybot.discord.service;

import pink.zak.giveawaybot.discord.service.text.Replace;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class BotConstants {
    private static final String version = BotConstants.class.getPackage().getImplementationVersion();
    private static final Replace baseReplace = replacer -> replacer.set("version", version);
    private static final String deviceName = getInitDeviceName();

    private static String getInitDeviceName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
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
}
