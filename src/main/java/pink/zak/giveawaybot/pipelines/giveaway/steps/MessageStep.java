package pink.zak.giveawaybot.pipelines.giveaway.steps;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
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
import pink.zak.giveawaybot.service.types.UserUtils;

import java.math.BigInteger;
import java.util.List;
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

    public void handleFinishedMessages(Server server, RichGiveaway giveaway, Message message, Set<Long> winners, BigInteger totalEntries) {
        this.handleFinishedMessages(server, giveaway, message, winners, totalEntries, null, false);
    }

    public void handleFinishedMessages(Server server, RichGiveaway giveaway, Message message, Set<Long> winners, BigInteger totalEntries, Map<Long, BigInteger> userEntries, boolean addToFinished) {
        StringBuilder descriptionBuilder = new StringBuilder();
        for (long winnerId : winners) {
            descriptionBuilder.append("<@").append(winnerId).append(">\n");
        }
        String description = descriptionBuilder.toString();
        message.editMessage(new EmbedBuilder()
                .setColor(this.palette.success())
                .setTitle(this.languageRegistry.get(server, Text.GIVEAWAY_EMBED_TITLE, replacer -> replacer.set("item", giveaway.giveawayItem())).get())
                .setDescription(this.languageRegistry.get(server, winners.size() > 1 ? Text.GIVEAWAY_FINISHED_EMBED_DESCRIPTION_PLURAL : Text.GIVEAWAY_FINISHED_EMBED_DESCRIPTION_SINGULAR,
                        replacer -> replacer.set("winners", description)).get())
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

        this.handleNewMessages(server, message.getTextChannel(), winners, preset, description);
    }

    private void handleNewMessages(Server server, TextChannel channel, Set<Long> winners, Preset preset, String description) {
        if (preset.getSetting(Setting.PING_WINNERS)) {
            if (preset.getSetting(Setting.WINNERS_MESSAGE)) {
                this.sendEndWithPing(server, channel, winners);
            } else {
                this.sendGhostPing(channel, description);
            }
        } else {
            if (preset.getSetting(Setting.WINNERS_MESSAGE)) {
                this.sendEndWithoutPing(server, channel, winners);
            }
        }
    }

    private void sendEndWithPing(Server server, TextChannel channel, Set<Long> winners) {
        List<String> winnerEntries = Lists.newArrayList();
        for (long winnerId : winners) {
            winnerEntries.add("<@" + winnerId + ">");
        }
        this.sendEnd(server, channel, winnerEntries);
    }

    private void sendEndWithoutPing(Server server, TextChannel channel, Set<Long> winners) {
        Guild guild = channel.getGuild();
        List<String> winnerEntries = Lists.newArrayList();
        for (long winnerId : winners) {
            Member member = guild.getMemberById(winnerId);
            if (member == null) {
                member = guild.retrieveMemberById(winnerId).complete();
            }
            winnerEntries.add(UserUtils.getNameDiscrim(member));
        }
        this.sendEnd(server, channel, winnerEntries);
    }

    private void sendEnd(Server server, TextChannel channel, List<String> winnerEntries) {
        String message;
        if (winnerEntries.size() == 1) {
            //message = this.languageRegistry.get(server, Text).replace(replacer -> replacer.set("winner", winnerEntries.get(0)));
        } else {
            int lastIndex = winnerEntries.size() - 1;
            String endEntry = winnerEntries.get(lastIndex);
            winnerEntries.remove(lastIndex);

            String winnerSection = String.join(", ", winnerEntries);

           // message = this.languageRegistry.get(server, Text).replace(replacer -> replacer.set("winners", winnerSection).set("last-winner", endEntry));
        }
        //channel.sendMessage(message).queue();
    }

    private void sendGhostPing(TextChannel channel, String description) {
        channel.sendMessage(description).queue(message -> message.delete().queue());
    }
}
