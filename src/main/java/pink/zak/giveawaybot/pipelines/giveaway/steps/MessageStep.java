package pink.zak.giveawaybot.pipelines.giveaway.steps;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.data.Defaults;
import pink.zak.giveawaybot.data.models.Preset;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.data.models.giveaway.RichGiveaway;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.text.Replace;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessageStep {
    private final Palette palette;
    private final Preset defaultPreset;
    private final LanguageRegistry languageRegistry;
    private final ShardManager shardManager;

    private final DeletionStep deletionStep;

    public MessageStep(GiveawayBot bot, GiveawayController giveawayController) {
        this.palette = bot.getDefaults().getPalette();
        this.defaultPreset = Defaults.defaultPreset;
        this.languageRegistry = bot.getLanguageRegistry();
        this.shardManager = bot.getShardManager();

        this.deletionStep = new DeletionStep(bot, giveawayController);
    }

    public MessageStep(GiveawayBot bot) {
        this(bot, bot.getGiveawayController());
    }

    public void sendEmptyMessage(CurrentGiveaway giveaway, Server server, Message message) {
        server.getActiveGiveaways().remove(giveaway.getMessageId());
        message.editMessage(new EmbedBuilder()
                .setColor(this.palette.success())
                .setTitle(this.languageRegistry.get(server, Text.GIVEAWAY_EMBED_TITLE, replacer -> replacer.set("item", giveaway.getGiveawayItem())).toString())
                .setDescription(this.languageRegistry.get(server, Text.GIVEAWAY_FINISHED_EMBED_DESCRIPTION_NO_WINNERS).toString())
                .setFooter(this.languageRegistry.get(server, Text.GIVEAWAY_FINISHED_EMBED_FOOTER_NO_WINNERS).toString()).build()).queue();
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
                .setTitle(this.languageRegistry.get(server, Text.GIVEAWAY_EMBED_TITLE, replacer -> replacer.set("item", giveaway.getGiveawayItem())).toString())
                .setDescription(this.languageRegistry.get(server, winners.size() > 1 ? Text.GIVEAWAY_FINISHED_EMBED_DESCRIPTION_PLURAL : Text.GIVEAWAY_FINISHED_EMBED_DESCRIPTION_SINGULAR,
                        replacer -> replacer.set("winners", description)).toString())
                .setFooter(this.languageRegistry.get(server, winners.size() > 1 ? Text.GIVEAWAY_FINISHED_EMBED_FOOTER_PLURAL : Text.GIVEAWAY_FINISHED_EMBED_FOOTER_SINGULAR,
                        replacer -> replacer.set("winner-count", winners.size()).set("entries", totalEntries)).toString())
                .build()).queue(sentMessage -> {
            // Here so only if the message is sent is the giveaway deleted
            if (giveaway instanceof CurrentGiveaway currentGiveaway) {
                this.deletionStep.delete(currentGiveaway);
                if (addToFinished) {
                    this.deletionStep.addToFinished(server, (CurrentGiveaway) giveaway, totalEntries, userEntries, winners);
                }
            }
        });
        // Handle the pinging of winners
        Preset preset = giveaway.getPresetName().equals("default") ? this.defaultPreset : server.getPreset(giveaway.getPresetName());

        this.handleNewMessages(server, giveaway, message.getTextChannel(), winners, preset, description);
        this.handleDm(server, giveaway, winners, preset);
    }

    private void handleDm(Server server, RichGiveaway giveaway, Set<Long> winners, Preset preset) {
        if (preset.getSetting(Setting.DM_WINNERS)) {
            Guild guild = this.shardManager.getGuildById(server.getId());
            if (guild == null) {
                JdaBot.LOGGER.warn("handleDm server should never be null but is null {} {}", giveaway.getServerId(), giveaway.getMessageId());
                return;
            }
            for (long winnerId : winners) {
                User user = this.shardManager.getUserById(winnerId);
                if (user == null) {
                    user = this.shardManager.retrieveUserById(winnerId).complete();
                    if (user == null) {
                        JdaBot.LOGGER.warn("handleDm user should never be null but is null {} {} {}", giveaway.getServerId(), giveaway.getMessageId(), winnerId);
                    }
                    continue;
                }
                user.openPrivateChannel().queue(privateChannel -> {
                    this.languageRegistry.get(server, Text.GIVEAWAY_FINISHED_WINNER_DM, replacer -> replacer.set("item", giveaway.getGiveawayItem()).set("server-name", guild.getName())).to(privateChannel);
                }, ex -> {
                    // ignored
                });
            }
        }
    }

    private void handleNewMessages(Server server, RichGiveaway giveaway, TextChannel channel, Set<Long> winners, Preset preset, String description) {
        if (preset.getSetting(Setting.PING_WINNERS)) {
            if (preset.getSetting(Setting.WINNERS_MESSAGE)) {
                this.sendEndWithPing(server, giveaway, channel, winners);
            } else {
                this.sendGhostPing(channel, description);
            }
        } else {
            if (preset.getSetting(Setting.WINNERS_MESSAGE)) {
                this.sendEndWithoutPing(server, giveaway, channel, winners);
            }
        }
    }

    private void sendEndWithPing(Server server, RichGiveaway giveaway, TextChannel channel, Set<Long> winners) {
        List<String> winnerEntries = Lists.newArrayList();
        for (long winnerId : winners) {
            winnerEntries.add("<@" + winnerId + ">");
        }
        this.sendEnd(server, giveaway, channel, winnerEntries);
    }

    private void sendEndWithoutPing(Server server, RichGiveaway giveaway, TextChannel channel, Set<Long> winners) {
        Guild guild = channel.getGuild();
        List<String> winnerEntries = Lists.newArrayList();
        for (long winnerId : winners) {
            Member member = guild.getMemberById(winnerId);
            if (member == null) {
                member = guild.retrieveMemberById(winnerId).complete();
            }
            winnerEntries.add(member.getUser().getAsTag());
        }
        this.sendEnd(server, giveaway, channel, winnerEntries);
    }

    private void sendEnd(Server server, RichGiveaway giveaway, TextChannel channel, List<String> winnerEntries) {
        Replace baseReplace = replacer -> replacer.set("item", giveaway.getGiveawayItem()).set("message-link", giveaway.getMessageLink());
        String message;
        if (winnerEntries.size() == 1) {
            message = this.languageRegistry.get(server, Text.GIVEAWAY_FINISHED_WINNER_MESSAGE).replace(replacer -> replacer.set("winner", winnerEntries.get(0)).addReplaces(baseReplace)).toString();
        } else {
            int lastIndex = winnerEntries.size() - 1;
            String endEntry = winnerEntries.get(lastIndex);
            winnerEntries.remove(lastIndex);

            String winnerSection = String.join(", ", winnerEntries);
            message = this.languageRegistry.get(server, Text.GIVEAWAY_FINISHED_WINNERS_MESSAGE).replace(replacer -> replacer.set("winners", winnerSection).set("last-winner", endEntry).addReplaces(baseReplace)).toString();
        }
        channel.sendMessage(message).queue();
    }

    private void sendGhostPing(TextChannel channel, String description) {
        channel.sendMessage(description).queue(message -> message.delete().queue());
    }
}
