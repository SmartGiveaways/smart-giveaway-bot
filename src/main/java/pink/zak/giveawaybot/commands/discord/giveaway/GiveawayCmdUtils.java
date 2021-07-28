package pink.zak.giveawaybot.commands.discord.giveaway;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
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

    public void schedule(Server server, String presetName, long timeUntil, long lengthMillis, SlashCommandEvent event, TextChannel giveawayChannel, long winnerAmount, String giveawayItem) {
        long startTime = System.currentTimeMillis() + timeUntil;
        long endTime = startTime + lengthMillis;
        if (this.performChecks(server, startTime, endTime, lengthMillis, winnerAmount, giveawayChannel, giveawayItem, event)) {
            return;
        }
        int intWinnerAmount = (int) winnerAmount;
        if (timeUntil < 120000) {
            this.lang.get(server, Text.SCHEDULED_TIME_TOO_SOON).to(event, true);
            return;
        }
        if (timeUntil > TimeIdentifier.MONTH.getMilliseconds()) {
            this.lang.get(server, Text.SCHEDULED_TIME_TOO_FAR_AWAY).to(event, true);
            return;
        }
        ImmutablePair<ScheduledGiveaway, ReturnCode> returnedInfo = this.scheduledGiveawayController.schedule(server, presetName, startTime, endTime, giveawayChannel, intWinnerAmount, giveawayItem);
        switch (returnedInfo.getValue()) {
            case GIVEAWAY_LIMIT_FAILURE -> this.lang.get(server, Text.SCHEDULED_GIVEAWAY_LIMIT_FAILURE).to(event, true);
            case FUTURE_GIVEAWAY_LIMIT_FAILURE -> this.lang.get(server, Text.SCHEDULED_GIVEAWAY_LIMIT_FAILURE_FUTURE).to(event, true);
            case NO_PRESET -> this.lang.get(server, Text.NO_PRESET_FOUND_ON_CREATION).to(event, true);
            case PERMISSIONS_FAILURE -> UserUtils.sendMissingPermsMessage(this.lang, server, event.getGuild().getSelfMember(), giveawayChannel, event);
            case SUCCESS -> this.lang.get(server, Text.GIVEAWAY_SCHEDULED, replacer -> replacer
                .set("channel", giveawayChannel.getAsMention())
                .set("time", returnedInfo.getKey().getStartFormatted())).to(event, true);
            default -> JdaBot.LOGGER.error("You messed up bad. GiveawayCmdUtils 1");
        }
    }

    public void create(Server server, long lengthMillis, long winnerAmount, String presetName, String giveawayItem, TextChannel giveawayChannel, SlashCommandEvent event) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + lengthMillis;
        int intWinnerAmount = (int) winnerAmount;
        if (this.performChecks(server, startTime, endTime, lengthMillis, winnerAmount, giveawayChannel, giveawayItem, event)) {
            return;
        }
        switch (this.giveawayController.createGiveaway(server, lengthMillis, endTime, intWinnerAmount, giveawayChannel, presetName, giveawayItem).getValue()) {
            case GIVEAWAY_LIMIT_FAILURE:
                this.lang.get(server, server.isPremium() ? Text.GIVEAWAY_LIMIT_FAILURE_PREMIUM : Text.GIVEAWAY_LIMIT_FAILURE).to(event, true);
                break;
            case NO_PRESET:
                this.lang.get(server, Text.NO_PRESET_FOUND_ON_CREATION).to(event, true);
                break;
            case PERMISSIONS_FAILURE:
                UserUtils.sendMissingPermsMessage(this.lang, server, giveawayChannel.getGuild().getSelfMember(), giveawayChannel, event);
                break;
            case GENERIC_FAILURE:
            case RATE_LIMIT_FAILURE:
                this.lang.get(server, Text.GENERIC_FAILURE).to(event, true);
                break;
            case UNKNOWN_EMOJI:
                this.lang.get(server, Text.UNKNOWN_EMOJI_ON_CREATION, replacer -> replacer.set("preset-name", presetName)).to(event, true);
                break;
            case SUCCESS:
                boolean sameChannel = event.getChannel().equals(giveawayChannel);
                this.lang.get(server, Text.GIVEAWAY_CREATED, replacer -> replacer.set("channel", giveawayChannel.getAsMention())).to(event, sameChannel);
                break;
            default:
                break;
        }
    }

    private boolean performChecks(Server server, long startTime, long endTime, long lengthMillis, long winnerAmount, TextChannel giveawayChannel, String giveawayItem, SlashCommandEvent event) {
        if (lengthMillis < (server.isPremium() ? 30000 : 300000)) {
            this.lang.get(server, server.isPremium() ? Text.GIVEAWAY_LENGTH_TOO_SHORT_PREMIUM : Text.GIVEAWAY_LENGTH_TOO_SHORT).to(event, true);
            return true;
        }
        if (lengthMillis > (server.isPremium() ? TimeIdentifier.MONTH.getMilliseconds() * 6 : TimeIdentifier.WEEK.getMilliseconds())) {
            this.lang.get(server, server.isPremium() ? Text.GIVEAWAY_LENGTH_TOO_LONG_PREMIUM : Text.GIVEAWAY_LENGTH_TOO_LONG).to(event, true);
            return true;
        }
        if (winnerAmount > 20) {
            this.lang.get(server, Text.WINNER_AMOUNT_TOO_LARGE).to(event, true);
            return true;
        }
        if (winnerAmount < 1) {
            this.lang.get(server, Text.WINNER_AMOUNT_TOO_SMALL).to(event, true);
            return true;
        }
        if (giveawayChannel == null) {
            this.lang.get(server, Text.COULDNT_FIND_CHANNEL).to(event, true);
            return true;
        }
        if (giveawayItem.isEmpty() || giveawayItem.replace(" ", "").isEmpty() || giveawayItem.length() > 35) {
            this.lang.get(server, Text.GIVEAWAY_ITEM_TOO_LONG).to(event, true);
            return true;
        }
        if (!server.getScheduledGiveaways().isEmpty() && this.giveawayController.getGiveawayCountAt(server, startTime, endTime) >= 10) {
            this.lang.get(server, Text.SCHEDULED_GIVEAWAY_LIMIT_FAILURE_FUTURE).to(event, true);
            return true;
        }
        return false;
    }
}
