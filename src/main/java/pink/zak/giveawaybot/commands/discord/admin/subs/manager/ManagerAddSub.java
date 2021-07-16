package pink.zak.giveawaybot.commands.discord.admin.subs.manager;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.SlashCommandUtils;
import pink.zak.giveawaybot.service.command.discord.DiscordCommandBase;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ManagerAddSub extends SubCommand {
    private final DiscordCommandBase commandBase;

    public ManagerAddSub(GiveawayBot bot, SimpleCommand parent) {
        super(bot, parent, "manager", "add", false, false);
        this.commandBase = bot.getDiscordCommandBase();
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        if (server.getManagerRoles().size() >= 5) {
            this.langFor(server, Text.ADMIN_MANAGER_LIMIT_REACHED).to(event);
            return;
        }
        Role role = event.getOption("role").getAsRole();

        if (server.getManagerRoles().contains(role.getIdLong())) {
            this.langFor(server, Text.ADMIN_MANAGER_ALREADY_CONTAINS).to(event);
            return;
        }
        server.getManagerRoles().add(role.getIdLong());
        SlashCommandUtils.updatePrivileges(event.getGuild(), server, this.commandBase);
        this.langFor(server, Text.ADMIN_MANAGER_ROLE_ADDED, replacer -> replacer.set("name", role.getName())).to(event);
    }
}
