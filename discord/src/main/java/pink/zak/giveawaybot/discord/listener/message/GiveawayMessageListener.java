package pink.zak.giveawaybot.discord.listener.message;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.models.Server;

public interface GiveawayMessageListener {

    void onExecute(Server server, GuildMessageReceivedEvent event);
}
