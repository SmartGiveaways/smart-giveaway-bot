package pink.zak.giveawaybot.commands.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.controller.GiveawayController;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;
import java.util.Set;

public class RerollSub extends SubCommand {
    private final FinishedGiveawayCache giveawayCache;
    private final ServerCache serverCache;
    private final GiveawayController giveawayController;

    public RerollSub(GiveawayBot bot) {
        super(bot, true);
        this.giveawayCache = bot.getFinishedGiveawayCache();
        this.serverCache = bot.getServerCache();
        this.giveawayController = bot.getGiveawayController();

        this.addFlat("reroll");
        this.addArgument(Long.class); // The giveaway message ID
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        this.giveawayCache.get(this.parseArgument(args, event.getGuild(), 1)).thenAccept(giveaway -> {
            if (giveaway == null) {
                event.getTextChannel().sendMessage(":x: Could not find a giveaway with that ID. Make sure the giveaway is finished and you're using the message ID.").queue();
                return;
            }
            if (System.currentTimeMillis() - giveaway.endTime() > 86400000) {
                event.getTextChannel().sendMessage(":x: You can only reroll a giveaway within 24 hours.").queue();
                return;
            }
            Set<Long> newWinners = this.giveawayController.generateWinners(giveaway.winnerAmount(), giveaway.totalEntries(), giveaway.userEntries());
            this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
                Message message = this.giveawayController.getGiveawayMessage(giveaway);
                if (message != null) {
                    this.giveawayController.handleGiveawayEndMessages(giveaway, newWinners, giveaway.totalEntries(), message, server);
                }
                Long[] winnersArray = newWinners.toArray(new Long[]{});
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
        });
    }
}
