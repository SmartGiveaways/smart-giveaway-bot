package pink.zak.giveawaybot.discord.listener.message;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.pipelines.entries.EntryPipeline;
import pink.zak.giveawaybot.discord.pipelines.entries.EntryType;

public class MessageSendListener implements GiveawayMessageListener {
    private final EntryPipeline entryPipeline;

    public MessageSendListener(GiveawayBot bot) {
        this.entryPipeline = bot.getEntryPipeline();
    }

    @Override
    public void onExecute(Server server, GuildMessageReceivedEvent event) {
        this.entryPipeline.process(EntryType.MESSAGES, server, event.getAuthor().getIdLong());
    }
}
