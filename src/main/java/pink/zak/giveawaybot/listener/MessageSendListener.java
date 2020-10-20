package pink.zak.giveawaybot.listener;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.entries.pipeline.EntryPipeline;
import pink.zak.giveawaybot.enums.EntryType;

public class MessageSendListener extends ListenerAdapter {
    private final EntryPipeline entryPipeline;

    public MessageSendListener(GiveawayBot bot) {
        this.entryPipeline = bot.getEntryPipeline();
    }

    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        this.entryPipeline.process(EntryType.MESSAGES, event.getGuild().getIdLong(), event.getAuthor().getIdLong());
    }
}
