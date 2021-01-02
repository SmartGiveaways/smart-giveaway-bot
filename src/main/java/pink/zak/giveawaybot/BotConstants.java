package pink.zak.giveawaybot;

import pink.zak.giveawaybot.service.text.Replace;

public class BotConstants {
    private static final String version = BotConstants.class.getPackage().getImplementationVersion();
    private static final Replace baseReplace = replacer -> replacer.set("version", version);

    public static String getVersion() {
        return version;
    }

    public static Replace getBaseReplace() {
        return baseReplace;
    }
}
