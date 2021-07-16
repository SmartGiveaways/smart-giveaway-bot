package pink.zak.giveawaybot.service;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.service.command.discord.DiscordCommandBase;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class SlashCommandUtils {

    public static void updatePrivileges(Guild guild, GiveawayBot bot) {
        Server server = bot.getServerCache().get(guild.getIdLong());
        updatePrivileges(guild, server, bot.getDiscordCommandBase());
    }

    public static void updatePrivileges(Guild guild, Server server, DiscordCommandBase commandBase) {
        Set<CommandPrivilege> privileges = Sets.newHashSet();

        Set<Long> roleIds = Sets.newHashSet(server.getManagerRoles());
        roleIds.addAll(guild.getRoles().stream().filter(role -> role.hasPermission(Permission.ADMINISTRATOR)).map(Role::getIdLong).collect(Collectors.toSet()));

        for (long roleId : roleIds)
            privileges.add(new CommandPrivilege(CommandPrivilege.Type.ROLE, true, roleId));

        Map<String, Collection<? extends CommandPrivilege>> commandPrivileges = Maps.newHashMap();

        for (SimpleCommand command : commandBase.getCommands().values()) {
            commandPrivileges.put(command.getCommand().getId(), privileges);
        }
        guild.updateCommandPrivileges(commandPrivileges).queue();
    }
}
