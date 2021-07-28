package pink.zak.giveawaybot.commands.discord.admin.subs.manager;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.SlashCommandUtils;
import pink.zak.giveawaybot.service.command.discord.DiscordCommandBase;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

public class ManagerAddSub extends SubCommand {
    private final DiscordCommandBase commandBase;

    public ManagerAddSub(GiveawayBot bot) {
        super(bot, "manager", "add", false, false);
        this.commandBase = bot.getDiscordCommandBase();
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        if (server.getManagerRoles().size() >= 5) {
            this.langFor(server, Text.ADMIN_MANAGER_LIMIT_REACHED).to(event, true);
            return;
        }
        Role role = event.getOption("role").getAsRole();

        if (server.getManagerRoles().contains(role.getIdLong())) {
            this.langFor(server, Text.ADMIN_MANAGER_ALREADY_CONTAINS).to(event, true);
            return;
        }
        server.getManagerRoles().add(role.getIdLong());
        SlashCommandUtils.updatePrivileges(event.getGuild(), server, this.commandBase);
        this.langFor(server, Text.ADMIN_MANAGER_ROLE_ADDED, replacer -> replacer.set("name", role.getName())).to(event, true);
    }
}
