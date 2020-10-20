package pink.zak.giveawaybot.listener;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.entries.pipeline.EntryPipeline;
import pink.zak.giveawaybot.enums.EntryType;

public class ReactionAddListener extends ListenerAdapter {
    private final EntryPipeline entryPipeline;

    public ReactionAddListener(GiveawayBot bot) {
        this.entryPipeline = bot.getEntryPipeline();
    }

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot() || event.getReactionEmote().isEmote() || !event.getReactionEmote().getAsCodepoints().equals("U+1f389")) { // U+1f389 is the unicode for the expected reaction
            return;
        }
        this.entryPipeline.process(EntryType.REACTION, event.getGuild().getIdLong(), event.getUserIdLong());
    }
}
