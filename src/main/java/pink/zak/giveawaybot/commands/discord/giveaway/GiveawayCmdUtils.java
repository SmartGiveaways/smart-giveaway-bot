package pink.zak.giveawaybot.commands.discord.giveaway;

import net.dv8tion.jda.api.entities.TextChannel;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.controllers.ReturnCode;
import pink.zak.giveawaybot.controllers.ScheduledGiveawayController;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.data.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.time.TimeIdentifier;
import pink.zak.giveawaybot.service.tuple.ImmutablePair;
import pink.zak.giveawaybot.service.types.UserUtils;

public class GiveawayCmdUtils {
    private final GiveawayController giveawayController;
    private final ScheduledGiveawayController scheduledGiveawayController;
    private final LanguageRegistry lang;

    public GiveawayCmdUtils(GiveawayBot bot) {
        this.giveawayController = bot.getGiveawayController();
        this.scheduledGiveawayController = bot.getScheduledGiveawayController();
        this.lang = bot.getLanguageRegistry();
    }

    public void schedule(Server server, String presetName, long timeUntil, long lengthMillis, TextChannel responseChannel, TextChannel giveawayChannel, int winnerAmount, String giveawayItem) {
        long startTime = System.currentTimeMillis() + timeUntil;
        long endTime = startTime + lengthMillis;
        if (this.performChecks(server, startTime, endTime, lengthMillis, winnerAmount, giveawayChannel, giveawayItem, responseChannel)) {
            return;
        }
        if (timeUntil < 120000) {
            this.lang.get(server, Text.SCHEDULED_TIME_TOO_SOON).to(responseChannel);
            return;
        }
        if (timeUntil > TimeIdentifier.MONTH.getMilliseconds()) {
            this.lang.get(server, Text.SCHEDULED_TIME_TOO_FAR_AWAY).to(responseChannel);
            return;
        }
        ImmutablePair<ScheduledGiveaway, ReturnCode> returnedInfo = this.scheduledGiveawayController.schedule(server, presetName, startTime, endTime, giveawayChannel, winnerAmount, giveawayItem);
        switch (returnedInfo.getValue()) {
            case GIVEAWAY_LIMIT_FAILURE -> this.lang.get(server, Text.SCHEDULED_GIVEAWAY_LIMIT_FAILURE).to(responseChannel);
            case FUTURE_GIVEAWAY_LIMIT_FAILURE -> this.lang.get(server, Text.SCHEDULED_GIVEAWAY_LIMIT_FAILURE_FUTURE).to(responseChannel);
            case NO_PRESET -> this.lang.get(server, Text.NO_PRESET_FOUND_ON_CREATION).to(responseChannel);
            case PERMISSIONS_FAILURE -> UserUtils.sendMissingPermsMessage(this.lang, server, responseChannel.getGuild().getSelfMember(), giveawayChannel, responseChannel);
            case SUCCESS -> this.lang.get(server, Text.GIVEAWAY_SCHEDULED, replacer -> replacer
                    .set("channel", giveawayChannel.getAsMention())
                    .set("time", returnedInfo.getKey().getStartFormatted())).to(responseChannel);
            default -> JdaBot.LOGGER.error("You messed up bad. GiveawayCmdUtils 1");
        }
    }

    public void create(Server server, long lengthMillis, int winnerAmount, String presetName, String giveawayItem, TextChannel giveawayChannel, TextChannel responseChannel) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + lengthMillis;
        if (this.performChecks(server, startTime, endTime, lengthMillis, winnerAmount, giveawayChannel, giveawayItem, responseChannel)) {
            return;
        }
        switch (this.giveawayController.createGiveaway(server, lengthMillis, endTime, winnerAmount, giveawayChannel, presetName, giveawayItem).getValue()) {
            case GIVEAWAY_LIMIT_FAILURE:
                this.lang.get(server, server.isPremium() ? Text.GIVEAWAY_LIMIT_FAILURE_PREMIUM : Text.GIVEAWAY_LIMIT_FAILURE).to(responseChannel);
                break;
            case NO_PRESET:
                this.lang.get(server, Text.NO_PRESET_FOUND_ON_CREATION).to(responseChannel);
                break;
            case PERMISSIONS_FAILURE:
                UserUtils.sendMissingPermsMessage(this.lang, server, giveawayChannel.getGuild().getSelfMember(), giveawayChannel, responseChannel);
                break;
            case GENERIC_FAILURE:
            case RATE_LIMIT_FAILURE:
                this.lang.get(server, Text.GENERIC_FAILURE).to(responseChannel);
                break;
            case UNKNOWN_EMOJI:
                this.lang.get(server, Text.UNKNOWN_EMOJI_ON_CREATION, replacer -> replacer.set("preset-name", presetName)).to(responseChannel);
                break;
            case SUCCESS:
                if (!responseChannel.equals(giveawayChannel)) {
                    this.lang.get(server, Text.GIVEAWAY_CREATED, replacer -> replacer.set("channel", giveawayChannel.getAsMention())).to(responseChannel);
                }
                break;
            default:
                break;
        }
    }

    private boolean performChecks(Server server, long startTime, long endTime, long lengthMillis, int winnerAmount, TextChannel giveawayChannel, String giveawayItem, TextChannel responseChannel) {
        if (lengthMillis < (server.isPremium() ? 30000 : 300000)) {
            this.lang.get(server, server.isPremium() ? Text.GIVEAWAY_LENGTH_TOO_SHORT_PREMIUM : Text.GIVEAWAY_LENGTH_TOO_SHORT).to(responseChannel);
            return true;
        }
        if (lengthMillis > (server.isPremium() ? TimeIdentifier.MONTH.getMilliseconds() * 6 : TimeIdentifier.WEEK.getMilliseconds())) {
            this.lang.get(server, server.isPremium() ? Text.GIVEAWAY_LENGTH_TOO_LONG_PREMIUM : Text.GIVEAWAY_LENGTH_TOO_LONG).to(responseChannel);
            return true;
        }
        if (winnerAmount > 20) {
            this.lang.get(server, Text.WINNER_AMOUNT_TOO_LARGE).to(responseChannel);
            return true;
        }
        if (winnerAmount < 1) {
            this.lang.get(server, Text.WINNER_AMOUNT_TOO_SMALL).to(responseChannel);
            return true;
        }
        if (giveawayChannel == null) {
            this.lang.get(server, Text.COULDNT_FIND_CHANNEL).to(responseChannel);
            return true;
        }
        if (giveawayItem.isEmpty() || giveawayItem.equals(" ") || giveawayItem.length() > 20) { // TODO add separate message for item too long and increase max length
            this.lang.get(server, Text.GIVEAWAY_ITEM_TOO_LONG).to(responseChannel);
            return true;
        }
        if (!server.getScheduledGiveaways().isEmpty() && this.giveawayController.getGiveawayCountAt(server, startTime, endTime) >= 10) {
            this.lang.get(server, Text.SCHEDULED_GIVEAWAY_LIMIT_FAILURE_FUTURE).to(responseChannel);
            return true;
        }
        return false;
    }
}
