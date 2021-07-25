package pink.zak.giveawaybot.listener;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.SlashCommandUtils;

public class PermissionUpdateListener extends ListenerAdapter {
    private final GiveawayBot bot;

    public PermissionUpdateListener(GiveawayBot bot) {
        this.bot = bot;
    }

    @Override
    public void onRoleUpdatePermissions(@NotNull RoleUpdatePermissionsEvent event) {
        if (
            (!event.getOldPermissions().contains(Permission.ADMINISTRATOR) && event.getNewPermissions().contains(Permission.ADMINISTRATOR))
                || (event.getOldPermissions().contains(Permission.ADMINISTRATOR) && !event.getNewPermissions().contains(Permission.ADMINISTRATOR))
        ) {
            SlashCommandUtils.updatePrivileges(event.getGuild(), this.bot);
        }
    }
}
