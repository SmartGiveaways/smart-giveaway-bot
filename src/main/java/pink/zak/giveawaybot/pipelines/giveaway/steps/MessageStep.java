package pink.zak.giveawaybot.pipelines.giveaway.steps;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.models.giveaway.RichGiveaway;
import pink.zak.giveawaybot.service.colour.Palette;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

public class MessageStep {
    private final Palette palette;
    private final Preset defaultPreset;
    private final LanguageRegistry languageRegistry;

    private final DeletionStep deletionStep;

    public MessageStep(GiveawayBot bot, GiveawayController giveawayController) {
        this.palette = bot.getDefaults().getPalette();
        this.defaultPreset = bot.getDefaults().getDefaultPreset();
        this.languageRegistry = bot.getLanguageRegistry();

        this.deletionStep = new DeletionStep(bot, giveawayController);
    }

    public MessageStep(GiveawayBot bot) {
        this(bot, bot.getGiveawayController());
    }

    public void sendEmptyMessage(CurrentGiveaway giveaway, Server server, Message message) {
        server.getActiveGiveaways().remove(giveaway.channelId());
        message.editMessage(new EmbedBuilder()
                .setColor(this.palette.success())
                .setTitle(this.languageRegistry.get(server, Text.GIVEAWAY_EMBED_TITLE, replacer -> replacer.set("item", giveaway.giveawayItem())).get())
                .setDescription(this.languageRegistry.get(server, Text.GIVEAWAY_FINISHED_EMBED_DESCRIPTION_NO_WINNERS).get())
                .setFooter(this.languageRegistry.get(server, Text.GIVEAWAY_FINISHED_EMBED_FOOTER_NO_WINNERS).get()).build()).queue();
    }

    public void sendFinishedMessage(Server server, RichGiveaway giveaway, Message message, Set<Long> winners, BigInteger totalEntries) {
        this.sendFinishedMessage(server, giveaway, message, winners, totalEntries, null, false);
    }

    public void sendFinishedMessage(Server server, RichGiveaway giveaway, Message message, Set<Long> winners, BigInteger totalEntries, Map<Long, BigInteger> userEntries, boolean addToFinished) {
        StringBuilder descriptionBuilder = new StringBuilder();
        for (long winnerId : winners) {
            descriptionBuilder.append("<@").append(winnerId).append(">\n");
        }
        message.editMessage(new EmbedBuilder()
                .setColor(this.palette.success())
                .setTitle(this.languageRegistry.get(server, Text.GIVEAWAY_EMBED_TITLE, replacer -> replacer.set("item", giveaway.giveawayItem())).get())
                .setDescription(this.languageRegistry.get(server, winners.size() > 1 ? Text.GIVEAWAY_FINISHED_EMBED_DESCRIPTION_PLURAL : Text.GIVEAWAY_FINISHED_EMBED_DESCRIPTION_SINGULAR,
                        replacer -> replacer.set("winners", descriptionBuilder.toString())).get())
                .setFooter(this.languageRegistry.get(server, winners.size() > 1 ? Text.GIVEAWAY_FINISHED_EMBED_FOOTER_PLURAL : Text.GIVEAWAY_FINISHED_EMBED_FOOTER_SINGULAR,
                        replacer -> replacer.set("winner-count", giveaway.winnerAmount()).set("entries", totalEntries)).get())
                .build()).queue(sentMessage -> {
                    if (giveaway instanceof CurrentGiveaway currentGiveaway) {
                        this.deletionStep.delete(currentGiveaway);
                        if (addToFinished) {
                            this.deletionStep.addToFinished((CurrentGiveaway) giveaway, totalEntries, userEntries,winners);
                        }
                    }
        }); // Here so only if the message is sent is the giveaway deleted
        // Handle the pinging of winners
        Preset preset = giveaway.presetName().equals("default") ? this.defaultPreset : server.getPreset(giveaway.presetName());
        if (preset.getSetting(Setting.PING_WINNERS)) {
            message.getTextChannel().sendMessage(descriptionBuilder.toString()).queue(sentMessage -> sentMessage.delete().queue());
        }
    }
}
