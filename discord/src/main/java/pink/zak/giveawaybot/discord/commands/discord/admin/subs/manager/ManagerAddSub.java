package pink.zak.giveawaybot.discord.commands.discord.admin.subs.manager;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.lang.Text;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;

import java.util.List;

public class ManagerAddSub extends SubCommand {

    public ManagerAddSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.addFlatWithAliases("role", "manager");
        this.addFlat("add");
        this.addArgument(Role.class);
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        if (server.getManagerRoles().size() >= 5) {
            this.langFor(server, Text.ADMIN_MANAGER_LIMIT_REACHED).to(event.getChannel());
            return;
        }
        Role role = this.parseArgument(args, event.getGuild(), 2);
        if (role == null) {
            this.langFor(server, Text.COULDNT_FIND_ROLE).to(event.getChannel());
            return;
        }
        if (server.getManagerRoles().contains(role.getIdLong())) {
            this.langFor(server, Text.ADMIN_MANAGER_ALREADY_CONTAINS).to(event.getChannel());
            return;
        }
        server.getManagerRoles().add(role.getIdLong());
        this.langFor(server, Text.ADMIN_MANAGER_ROLE_ADDED, replacer -> replacer.set("name", role.getName())).to(event.getChannel());
    }
}
