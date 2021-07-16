package pink.zak.giveawaybot.listener.slash;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.data.models.Server;

public interface SlashCommandListener {

    void onSlashCommand(Server server, SlashCommandEvent event);
}
