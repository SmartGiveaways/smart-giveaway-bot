package pink.zak.giveawaybot.discord.service.types;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import pink.zak.giveawaybot.discord.data.Defaults;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.lang.LanguageRegistry;
import pink.zak.giveawaybot.discord.lang.Text;
import pink.zak.giveawaybot.discord.service.tuple.ImmutablePair;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class UserUtils {

    public ImmutablePair<ImmutableSet<Permission>, String> getMissingPerms(Member selfMember, TextChannel channel) {
        Set<Permission> missingPerms = Sets.newHashSet(Defaults.requiredPermissions);
        missingPerms.removeAll(selfMember.getPermissions(channel));
        return ImmutablePair.of(Sets.immutableEnumSet(missingPerms), missingPerms.stream().map(Permission::getName).map(str -> "`" + str + "`").collect(Collectors.joining(", ")));
    }

    public void sendMissingPermsMessage(LanguageRegistry languageRegistry, Server server, Member selfMember, TextChannel checkChannel, TextChannel responseChannel) {
        ImmutablePair<ImmutableSet<Permission>, String> permissionPair = UserUtils.getMissingPerms(selfMember, checkChannel);
        languageRegistry.get(server, permissionPair.getKey().size() > 1 ? Text.BOT_MISSING_PERMISSIONS_SPECIFIC : Text.BOT_MISSING_PERMISSION_SPECIFIC,
                replacer -> replacer.set("permission", permissionPair.getValue())).to(responseChannel);
    }

    public long parseIdInput(String input) {
        if (input.contains(" ") || input.length() < 18 || input.length() > 26)
            return -1;
        String toParseNew;
        char firstChar = input.charAt(0);
        if (firstChar == '<') {
            int startIndex;
            char secondChar = input.charAt(1);
            if (secondChar == '#') {
                startIndex = 2;
            } else if (secondChar == '@') {
                char thirdChar = input.charAt(2);
                if (thirdChar == '!' || thirdChar == '&')
                    startIndex = 3;
                else
                    startIndex = 2;
            } else {
                return -1;
            }
            int closingPosition = 0;
            for (int i = 1; i < input.length(); i++) {
                if (input.charAt(i) == '>')
                    closingPosition = i;
            }
            if (closingPosition == 0)
                return -1;
            toParseNew = input.substring(startIndex, closingPosition);
        } else {
            toParseNew = input;
        }
        for (char character : toParseNew.toCharArray()) {
            if (!Character.isDigit(character))
                return -1;
        }
        try {
            return Long.parseLong(toParseNew);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
