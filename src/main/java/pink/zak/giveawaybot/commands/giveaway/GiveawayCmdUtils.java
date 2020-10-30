package pink.zak.giveawaybot.commands.giveaway;

import net.dv8tion.jda.api.entities.TextChannel;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.models.Server;

public class GiveawayCmdUtils {
    private final GiveawayController giveawayController;

    public GiveawayCmdUtils(GiveawayBot bot) {
        this.giveawayController = bot.getGiveawayController();
    }

    public void create(Server server, long lengthMillis, int winnerAmount, String presetName, String giveawayItem, TextChannel giveawayChannel, TextChannel responseChannel) {
        if (this.performChecks(lengthMillis, winnerAmount, giveawayChannel, giveawayItem, responseChannel)) {
            return;
        }
        switch (this.giveawayController.createGiveaway(server, lengthMillis, winnerAmount, giveawayChannel, presetName, giveawayItem).getRight()) {
            case GIVEAWAY_LIMIT_FAILURE:
                responseChannel.sendMessage(":x: Your guild has reached the maximum number of 5 giveaways. Gimme all ur money for more or delete some by deleting their message.").queue();
                break;
            case WINNER_LIMIT_FAILURE:
                responseChannel.sendMessage(":x: You can only have a maximum of 5 winners per giveaway. Gimme all ur money if u want more.").queue();
                break;
            case NO_PRESET:
                responseChannel.sendMessage(":x: Could not find the specified preset. Use `>preset list` to list them all.").queue();
                break;
            case GENERIC_FAILURE:
            case RATE_LIMIT_FAILURE:
                responseChannel.sendMessage(":x: An error occurred. Try again in a minute when the bot is less busy.").queue();
                break;
            case UNKNOWN_EMOJI:
                responseChannel.sendMessage(":x: Your giveaway was created but the reaction could not be added. Check the preset `" + presetName + "` and make sure the reaction is valid. You must manually add it if you change it.").queue();
                break;
            case SUCCESS:
                if (responseChannel != giveawayChannel) {
                    responseChannel.sendMessage(":white_check_mark: Created your giveaway in " + giveawayChannel.getAsMention() + ".").queue();
                }
        }
    }

    private boolean performChecks(long length, int winnerAmount, TextChannel giveawayChannel, String giveawayItem, TextChannel responseChannel) {
        if (length < 30000) {
            responseChannel.sendMessage("Giveaways must be at least 30 seconds long.").queue();
            return true;
        }
        if (winnerAmount > 20) {
            responseChannel.sendMessage("Giveaways must have less than 20 winners.").queue();
            return true;
        }
        if (winnerAmount < 1) {
            responseChannel.sendMessage("Giveaways must have at least 1 winner.").queue();
            return true;
        }
        if (length > 5184000000L) {
            responseChannel.sendMessage("Giveaways must be no longer than 30 days.").queue();
            return true;
        }
        if (giveawayChannel == null) {
            responseChannel.sendMessage("Could not find the specified channel.").queue();
            return true;
        }
        if (giveawayItem.isEmpty() || giveawayItem.equals(" ") || giveawayItem.length() > 20) {
            responseChannel.sendMessage("Issue parsing giveaway reward.").queue();
            return true;
        }
        return false;
    }
}
