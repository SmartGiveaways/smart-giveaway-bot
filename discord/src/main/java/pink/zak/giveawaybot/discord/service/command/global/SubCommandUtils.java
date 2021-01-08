package pink.zak.giveawaybot.discord.service.command.global;

import com.google.common.collect.Lists;

import java.util.List;

public class SubCommandUtils {

    public static String[] getEnd(int argSize, List<String> arguments) {
        List<String> newList = Lists.newArrayList();
        for (int i = 0; i < arguments.size(); i++) {
            if (i < argSize - 1) {
                continue;
            }
            newList.add(arguments.get(i));
        }
        return newList.toArray(new String[]{});
    }
}
