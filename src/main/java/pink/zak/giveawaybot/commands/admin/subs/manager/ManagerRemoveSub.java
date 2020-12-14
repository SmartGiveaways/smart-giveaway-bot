package pink.zak.giveawaybot.commands.admin.subs.manager;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public class ManagerRemoveSub extends SubCommand {

    public ManagerRemoveSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.addFlatWithAliases("role", "manager");
        this.addFlat("remove");
        this.addArgument(Role.class);
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        Role role = this.parseArgument(args, event.getGuild(), 2);
        if (role == null) {
            this.langFor(server, Text.COULDNT_FIND_ROLE).to(event.getChannel());
            return;
        }
        if (!server.getManagerRoles().contains(role.getIdLong())) {
            this.langFor(server, Text.ADMIN_MANAGER_DOESNT_CONTAIN).to(event.getChannel());
            return;
        }
        server.getManagerRoles().remove(role.getIdLong());
        this.langFor(server, Text.ADMIN_MANAGER_ROLE_REMOVED, replacer -> replacer.set("name", role.getName())).to(event.getChannel());
    }
}
