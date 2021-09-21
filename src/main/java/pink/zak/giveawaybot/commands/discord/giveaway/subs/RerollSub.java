package pink.zak.giveawaybot.commands.discord.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.data.cache.FinishedGiveawayCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.data.models.giveaway.finished.FullFinishedGiveaway;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.pipelines.giveaway.steps.MessageStep;
import pink.zak.giveawaybot.pipelines.giveaway.steps.WinnerStep;
import pink.zak.giveawaybot.service.command.discord.command.BotSubCommand;

import java.util.Set;

public class RerollSub extends BotSubCommand {
    private final FinishedGiveawayCache giveawayCache;
    private final GiveawayController giveawayController;
    private final MessageStep messageStep;
    private final WinnerStep winnerStep;

    public RerollSub(GiveawayBot bot) {
        super(bot, "reroll", true, false);
        this.giveawayCache = bot.getFinishedGiveawayCache();
        this.giveawayController = bot.getGiveawayController();
        this.messageStep = new MessageStep(bot);
        this.winnerStep = new WinnerStep(this.messageStep);
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        long giveawayId = event.getOption("giveawayid").getAsLong();
        if (giveawayId < 786066350882488381L) { // Just check the ID isn't too old to reduce hits on the database.
            this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(event, true);
            return;
        }
        FullFinishedGiveaway giveaway = this.giveawayCache.get(giveawayId);
        if (giveaway == null) {
            this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(event, true);
            return;
        }
        if (System.currentTimeMillis() - giveaway.getEndTime() > 86400000) {
            this.langFor(server, Text.REROLL_OVER_24_HOURS).to(event, true);
            return;
        }
        Set<Long> newWinners = this.winnerStep.regenerateWinners(giveaway);
        Message message = this.giveawayController.getGiveawayMessage(giveaway);
        giveaway.setWinners(newWinners);
        if (message != null) {
            this.messageStep.handleFinishedMessages(server, giveaway, message, newWinners, giveaway.getTotalEntries());
        }
        Long[] winnersArray = newWinners.toArray(new Long[]{});
        if (winnersArray.length == 1) {
            this.langFor(server, Text.REROLL_ONE_WINNER, replacer -> replacer.set("winner", "<@" + winnersArray[0] + ">")).to(event, true);
            return;
        }
        int count = 0;
        StringBuilder builder = new StringBuilder();
        for (long winner : winnersArray) {
            count += 1;
            if (count == winnersArray.length) {
                this.langFor(server, Text.REROLL_MULTIPLE_WINNERS, replacer -> replacer
                    .set("winners", builder.toString())
                    .set("final-winner", "<@" + winner + ">")).to(event, true);
                return;
            } else {
                builder.append("<@").append(winnersArray[count - 1]).append(">");
                if (count + 1 != winnersArray.length) { // Make it so that a , is not added if there will then be an "and" after.
                    builder.append(", ");
                }
            }
        }
    }
}
