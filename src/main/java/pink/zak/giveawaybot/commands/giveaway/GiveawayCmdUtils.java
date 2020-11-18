package pink.zak.giveawaybot.commands.giveaway;

import net.dv8tion.jda.api.entities.TextChannel;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;

public class GiveawayCmdUtils {
    private final GiveawayController giveawayController;
    private final LanguageRegistry lang;

    public GiveawayCmdUtils(GiveawayBot bot) {
        this.giveawayController = bot.getGiveawayController();
        this.lang = bot.getLanguageRegistry();
    }

    public void create(Server server, long lengthMillis, int winnerAmount, String presetName, String giveawayItem, TextChannel giveawayChannel, TextChannel responseChannel) {
        if (this.performChecks(server, lengthMillis, winnerAmount, giveawayChannel, giveawayItem, responseChannel)) {
            return;
        }
        switch (this.giveawayController.createGiveaway(server, lengthMillis, winnerAmount, giveawayChannel, presetName, giveawayItem).getValue()) {
            case GIVEAWAY_LIMIT_FAILURE:
                this.lang.get(server, Text.GIVEAWAY_LIMIT_FAILURE).to(responseChannel);
                break;
            case NO_PRESET:
                this.lang.get(server, Text.NO_PRESET_FOUND_ON_CREATION).to(responseChannel);
                break;
            case PERMISSIONS_FAILURE:
                this.lang.get(server, Text.BOT_DOESNT_HAVE_PERMISSIONS).to(responseChannel);
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

    private boolean performChecks(Server server, long length, int winnerAmount, TextChannel giveawayChannel, String giveawayItem, TextChannel responseChannel) {
        if (length < 30000) {
            this.lang.get(server, Text.GIVEAWAY_LENGTH_TOO_SHORT).to(responseChannel);
            return true;
        }
        if (length > 5184000000L) {
            this.lang.get(server, Text.GIVEAWAY_LENGTH_TOO_LONG).to(responseChannel);
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
        if (giveawayItem.isEmpty() || giveawayItem.equals(" ") || giveawayItem.length() > 20) {
            this.lang.get(server, Text.PARSING_REWARD_FAILED).to(responseChannel);
            return true;
        }
        return false;
    }
}
