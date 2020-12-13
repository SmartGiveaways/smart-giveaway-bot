package pink.zak.giveawaybot.commands.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
        super(bot, true, false, false);
        this.giveawayCache = bot.getFinishedGiveawayCache();
        this.giveawayController = bot.getGiveawayController();

        this.addFlat("reroll");
        this.addArgument(Long.class); // The giveaway message ID
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        long idInput = this.parseArgument(args, event.getGuild(), 1);
        TextChannel textChannel = event.getChannel();
        if (idInput < 779076362073145394L) { // Just check the ID isn't too old to reduce hits on the database.
            this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(textChannel);
            return;
        }
        this.giveawayCache.get(idInput).thenAccept(giveaway -> {
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
            if (winnersArray.length == 1) {
                this.langFor(server, Text.REROLL_ONE_WINNER, replacer -> replacer.set("winner", "<@" + winnersArray[0] + ">")).to(event.getChannel());
                return;
            }
            int count = 0;
            StringBuilder builder = new StringBuilder();
            for (long winner : winnersArray) {
                count += 1;
                if (count == winnersArray.length) {
                    this.langFor(server, Text.REROLL_MULTIPLE_WINNERS, replacer -> replacer
                            .set("winners", builder.toString())
                            .set("final-winner", "<@" + winner + ">")).to(event.getChannel());
                    return;
                } else {
                    builder.append("<@").append(winnersArray[count - 1]).append(">");
                    if (count + 1 != winnersArray.length) { // Make it so that a , is not added if there will then be an "and" after.
                        builder.append(", ");
                    }
                }
            }
        });
    }
}
