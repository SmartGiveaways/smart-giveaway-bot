package pink.zak.giveawaybot.listener.reaction.pageable;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public interface PageableReactionListener {

    void onReactionAdd(Page page, GuildMessageReactionAddEvent event);

    long getMessageId();
}
