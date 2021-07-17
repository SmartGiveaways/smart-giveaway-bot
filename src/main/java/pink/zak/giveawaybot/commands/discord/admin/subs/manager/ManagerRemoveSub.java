package pink.zak.giveawaybot.commands.discord.admin.subs.manager;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

public class ManagerRemoveSub extends SubCommand {

    public ManagerRemoveSub(GiveawayBot bot) {
        super(bot, "manager", "remove", false, false);
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        Role role = event.getOption("role").getAsRole();
        if (!server.getManagerRoles().contains(role.getIdLong())) {
            this.langFor(server, Text.ADMIN_MANAGER_DOESNT_CONTAIN).to(event);
            return;
        }
        server.getManagerRoles().remove(role.getIdLong());
        this.langFor(server, Text.ADMIN_MANAGER_ROLE_REMOVED, replacer -> replacer.set("name", role.getName())).to(event);
    }
}
