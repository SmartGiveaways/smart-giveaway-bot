package pink.zak.giveawaybot.commands.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;
import java.util.Set;

public class RerollSub extends SubCommand {
    private final FinishedGiveawayCache giveawayCache;
    private final GiveawayController giveawayController;

    public RerollSub(GiveawayBot bot) {
        super(bot, true);
        this.giveawayCache = bot.getFinishedGiveawayCache();
        this.giveawayController = bot.getGiveawayController();

        this.addFlat("reroll");
        this.addArgument(Long.class); // The giveaway message ID
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        this.giveawayCache.get(this.parseArgument(args, event.getGuild(), 1)).thenAccept(giveaway -> {
            TextChannel textChannel = event.getTextChannel();
            if (giveaway == null) {
                this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(textChannel);
                return;
            }
            if (System.currentTimeMillis() - giveaway.endTime() > 86400000) {
                this.langFor(server, Text.REROLL_OVER_24_HOURS).to(textChannel);
                return;
            }
            Set<Long> newWinners = this.giveawayController.regenerateWinners(giveaway);
            Message message = this.giveawayController.getGiveawayMessage(giveaway);
            if (message != null) {
                this.giveawayController.handleGiveawayEndMessages(giveaway, newWinners, giveaway.totalEntries(), message, server);
            }
            Long[] winnersArray = newWinners.toArray(new Long[]{});
            // TODO more messages
            StringBuilder builder = new StringBuilder(":white_check_mark: Your new winner");
            if (winnersArray.length == 1) {
                builder.append(" is <@").append(winnersArray[0]).append(">");
            } else {
                int count = 0;
                builder.append("s are ");
                for (long winner : winnersArray) {
                    count += 1;
                    if (count == winnersArray.length) {
                        builder.append("and <@").append(winner).append(">");
                    } else {
                        builder.append("<@").append(winnersArray[count - 1]).append("> ");
                    }
                }
            }
            event.getChannel().sendMessage(builder.toString()).queue();
        });
    }
}
