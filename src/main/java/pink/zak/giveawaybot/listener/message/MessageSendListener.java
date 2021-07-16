package pink.zak.giveawaybot.listener.message;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.pipelines.entries.EntryPipeline;
import pink.zak.giveawaybot.pipelines.entries.EntryType;

public class MessageSendListener implements GiveawayMessageListener {
    private final EntryPipeline entryPipeline;

    public MessageSendListener(GiveawayBot bot) {
        this.entryPipeline = bot.getEntryPipeline();
    }

    @Override
    public void onMessageReceived(Server server, GuildMessageReceivedEvent event) {
        this.entryPipeline.process(EntryType.MESSAGES, server, event.getAuthor().getIdLong());
    }
}
