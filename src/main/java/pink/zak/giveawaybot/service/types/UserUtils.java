package pink.zak.giveawaybot.service.types;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.Nullable;
import pink.zak.giveawaybot.data.Defaults;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.tuple.ImmutablePair;

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

    public void sendMissingPermsMessage(LanguageRegistry languageRegistry, Server server, Member selfMember, TextChannel checkChannel, SlashCommandEvent event) {
        ImmutablePair<ImmutableSet<Permission>, String> permissionPair = UserUtils.getMissingPerms(selfMember, checkChannel);
        languageRegistry.get(server, permissionPair.getKey().size() > 1 ? Text.BOT_MISSING_PERMISSIONS_SPECIFIC : Text.BOT_MISSING_PERMISSION_SPECIFIC,
                replacer -> replacer.set("permission", permissionPair.getValue())).to(event, true);
    }

    public long parseIdInput(String input) {
        if (input.contains(" ") || input.length() < 18 || input.length() > 26)
            return -1;
        String toParseNew;
        char firstChar = input.charAt(0);
        if (firstChar == '<') {
            toParseNew = parseEnclosedId(input);
            if (toParseNew == null)
                return -1;
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

    /**
     * Parses an ID enclosed in <> where it's know that it starts with <
     *
     * @param input Input of a possibly enclosed ID already partially sanitised.
     * @return The parsed ID or null if not legitimately enclosed
     */
    @Nullable
    private String parseEnclosedId(String input) {
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
            return null;
        }
        int closingPosition = 0;
        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i) == '>')
                closingPosition = i;
        }
        if (closingPosition == 0)
            return null;
        return input.substring(startIndex, closingPosition);
    }
}
