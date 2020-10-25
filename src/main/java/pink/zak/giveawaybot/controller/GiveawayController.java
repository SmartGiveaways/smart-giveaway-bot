package pink.zak.giveawaybot.controller;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.cache.UserCache;
import pink.zak.giveawaybot.enums.EntryType;
import pink.zak.giveawaybot.enums.ReturnCode;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.models.Giveaway;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.User;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.time.Time;
import pink.zak.giveawaybot.service.types.NumberUtils;
import pink.zak.giveawaybot.service.types.ReactionContainer;
import pink.zak.giveawaybot.storage.GiveawayStorage;
import pink.zak.giveawaybot.threads.ThreadFunction;
import pink.zak.giveawaybot.threads.ThreadManager;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GiveawayController {
    private final ThreadManager threadManager;
    private final GiveawayCache giveawayCache;
    private final GiveawayStorage giveawayStorage;
    private final ServerCache serverCache;
    private final Preset defaultPreset;
    private final Palette palette;
    private final GiveawayBot bot;

    public GiveawayController(GiveawayBot bot) {
        this.threadManager = bot.getThreadManager();
        this.giveawayCache = bot.getGiveawayCache();
        this.giveawayStorage = bot.getGiveawayStorage();
        this.serverCache = bot.getServerCache();
        this.defaultPreset = bot.getDefaults().getDefaultPreset();
        this.palette = bot.getDefaults().getPalette();
        this.bot = bot;
        this.loadAllGiveaways();
        this.startGiveawayUpdater();
    }

    public ImmutablePair<Giveaway, ReturnCode> createGiveaway(long length, int winnerAmount, TextChannel giveawayChannel, String presetName, String giveawayItem) {
        long serverId = giveawayChannel.getGuild().getIdLong();
        long endTime = System.currentTimeMillis() + length;
        return this.serverCache.get(serverId).thenApply(server -> {
            if (server.getActiveGiveaways().size() >= 50) {
                return ImmutablePair.of((Giveaway) null, ReturnCode.GIVEAWAY_LIMIT_FAILURE);
            }
            if (winnerAmount > 5 || winnerAmount < 1) {
                return ImmutablePair.of((Giveaway) null, ReturnCode.WINNER_LIMIT_FAILURE);
            }
            Preset preset = presetName.equalsIgnoreCase("default") ? this.defaultPreset : server.getPreset(presetName);
            if (preset == null) {
                return ImmutablePair.of((Giveaway) null, ReturnCode.NO_PRESET);
            }
            try {
                Message message = giveawayChannel.sendMessage(new EmbedBuilder()
                        .setTitle("Giveaway: ".concat(giveawayItem))
                        .setColor(this.palette.primary())
                        .setFooter("Ends in " + Time.format(length) + " with " + winnerAmount + " winner" + (winnerAmount > 1 ? "s" : ""))
                        .build()).complete(true);
                if ((boolean) preset.getSetting(Setting.ENABLE_REACT_TO_ENTER)) {
                    MessageReaction.ReactionEmote reaction = ((ReactionContainer) preset.getSetting(Setting.REACT_TO_ENTER_EMOJI)).getReactionEmote();
                    if (reaction.isEmoji()) {
                        message.addReaction(reaction.getEmoji()).queue();
                    } else {
                        message.addReaction(reaction.getEmote()).queue();
                    }
                }
                Giveaway giveaway = new Giveaway(message.getIdLong(), giveawayChannel.getIdLong(), giveawayChannel.getGuild().getIdLong(), endTime, winnerAmount, presetName, giveawayItem);
                this.giveawayCache.addGiveaway(giveaway);
                server.addActiveGiveaway(giveaway);
                this.startGiveawayTimer(giveaway);
                return ImmutablePair.of(giveaway, ReturnCode.SUCCESS);
            } catch (RateLimitedException ex) {
                GiveawayBot.getLogger().error("", ex);
                return ImmutablePair.of((Giveaway) null, ReturnCode.RATE_LIMIT_FAILURE);
            }
        }).join();
    }

    public void deleteGiveaway(Giveaway giveaway) {
        this.giveawayCache.invalidateAsync(giveaway.uuid(), false);
        this.serverCache.get(giveaway.serverId()).thenAccept(server -> {
            server.getActiveGiveaways().remove(giveaway.messageId());
            GiveawayBot.getLogger().info("Removing giveaway from server {}  :  {}", giveaway.serverId(), giveaway.uuid());
            UserCache userCache = server.getUserCache();
            for (long enteredId : giveaway.enteredUsers()) {
                userCache.get(enteredId).thenAccept(user -> user.entries().remove(giveaway.uuid()));
            }
        });
        this.giveawayStorage.delete(giveaway.uuid().toString());
    }

    public void loadAllGiveaways() {
        long loadStartTime = System.currentTimeMillis();
        this.bot.runAsync(ThreadFunction.STORAGE, () -> {
            for (Giveaway giveaway : this.giveawayStorage.loadAll()) {
                if (!giveaway.isActive()) {
                    this.endGiveaway(giveaway);
                    continue;
                }
                if (this.getGiveawayMessage(giveaway) == null) {
                    this.deleteGiveaway(giveaway);
                    continue;
                }
                this.giveawayCache.addGiveaway(giveaway);
                this.startGiveawayTimer(giveaway);
            }
            return null;
        }).whenComplete((o, ex) -> {
            if (ex != null) {
                GiveawayBot.getLogger().error("", ex);
            }
            GiveawayBot.getLogger().info("Loaded {} giveaways in {} milliseconds", this.giveawayCache.size(), System.currentTimeMillis() - loadStartTime);
        });
    }

    private void endGiveaway(Giveaway giveaway) {
        Message giveawayMessage = this.getGiveawayMessage(giveaway);
        if (giveawayMessage != null) {
            this.serverCache.get(giveaway.serverId()).thenAccept(server -> {
                BigInteger totalEntries = BigInteger.ZERO;
                Map<Long, BigInteger> userEntriesMap = Maps.newHashMap();
                for (long enteredUserId : giveaway.enteredUsers()) {
                    User user = server.getUserCache().getSync(enteredUserId);
                    if (user == null || user.isBanned() || user.isShadowBanned()) {
                        continue;
                    }
                    Map<EntryType, AtomicInteger> entries = user.entries().get(giveaway.uuid());
                    if (entries != null) {
                        BigInteger totalUserEntries = BigInteger.ZERO;
                        for (AtomicInteger entryTypeAmount : entries.values()) {
                            totalEntries = totalEntries.add(BigInteger.valueOf(entryTypeAmount.get()));
                            totalUserEntries = totalUserEntries.add(BigInteger.valueOf(entryTypeAmount.get()));
                        }
                        userEntriesMap.put(user.id(), totalUserEntries);
                    }
                }
                GiveawayBot.getLogger().info(userEntriesMap.toString());
                server.getActiveGiveaways().remove(giveaway.channelId());
                if (totalEntries.equals(BigInteger.ZERO)) {
                    giveawayMessage.editMessage(new EmbedBuilder()
                            .setColor(this.palette.success())
                            .setTitle("Giveaway: " + giveaway.giveawayItem())
                            .setDescription("There were not enough entries to determine winners.")
                            .setFooter("Ended with no winners.").build()).queue();
                    return;
                }
                Set<Long> winners = this.generateWinners(giveaway, totalEntries, userEntriesMap);
                GiveawayBot.getLogger().info("Giveaway {} generated winners {}", giveaway.uuid(), winners);
                StringBuilder descriptionBuilder = new StringBuilder();
                for (long winnerId : winners) {
                    descriptionBuilder.append("<@").append(winnerId).append(">\n");
                }
                giveawayMessage.editMessage(new EmbedBuilder()
                        .setColor(this.palette.success())
                        .setTitle("Giveaway: " + giveaway.giveawayItem())
                        .setDescription((winners.size() > 1 ? "**Winners:**\n" : "**Winner:**\n") + descriptionBuilder.toString())
                        .setFooter("Ended with " + winners.size() + " winners and " + totalEntries.toString() + " entries.").build()).queue();
                // Handle the pinging of winners
                Preset preset = giveaway.presetName().equals("default") ? this.defaultPreset : server.getPreset(giveaway.presetName());
                if ((boolean) preset.getSetting(Setting.PING_WINNERS)) {
                    giveawayMessage.getTextChannel().sendMessage(descriptionBuilder.toString()).queue(message -> message.delete().queue());
                }
            }).exceptionally(ex -> {
                GiveawayBot.getLogger().error("", ex);
                return null;
            });
        }
        this.deleteGiveaway(giveaway);
    }

    private Set<Long> generateWinners(Giveaway giveaway, BigInteger totalEntries, Map<Long, BigInteger> userEntries) {
        Set<Long> winners = Sets.newHashSet();
        if (userEntries.size() <= giveaway.winnerAmount()) {
            return userEntries.keySet();
        }
        BigInteger currentTotalEntries = totalEntries;
        for (int i = 1; i <= giveaway.winnerAmount(); i++) {
            BigInteger decreasingRandom = currentTotalEntries.divide(NumberUtils.getRandomBigInteger(currentTotalEntries));
            for (Map.Entry<Long, BigInteger> entry : userEntries.entrySet()) {
                decreasingRandom = decreasingRandom.subtract(entry.getValue());
                if (decreasingRandom.compareTo(BigInteger.ONE) < 0) {
                    winners.add(entry.getKey());
                    if (i + 1 <= giveaway.winnerAmount()) { // Prevent duplicates if it will be looped again
                        userEntries.remove(entry.getKey());
                        currentTotalEntries = currentTotalEntries.subtract(entry.getValue());
                    }
                    break;
                }
            }

        }
        return winners;
    }

    private void startGiveawayUpdater() {
        this.threadManager.getUpdaterExecutor().scheduleAtFixedRate(() -> {
            for (Giveaway giveaway : this.giveawayCache.getMap().values()) {
                if (!giveaway.isActive()) {
                    return;
                }
                Message message = this.getGiveawayMessage(giveaway);
                if (message == null) {
                    this.deleteGiveaway(giveaway);
                    return;
                }
                message.editMessage(new EmbedBuilder()
                        .setTitle("Giveaway: ".concat(giveaway.giveawayItem()))
                        .setColor(this.palette.primary())
                        .setFooter("Ends in " + Time.format(giveaway.getTimeToExpiry()) + " with " + giveaway.winnerAmount() + " winner" + (giveaway.winnerAmount() > 1 ? "s" : ""))
                        .build()).queue();
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    private void startGiveawayTimer(Giveaway giveaway) {
        this.threadManager.getUpdaterExecutor().schedule(() -> {
            GiveawayBot.getLogger().info("Giveaway {} expired", giveaway.uuid());
            this.endGiveaway(giveaway);
        }, giveaway.getTimeToExpiry(), TimeUnit.MILLISECONDS);
    }

    @SneakyThrows
    private Message getGiveawayMessage(Giveaway giveaway) {
        Guild guild = this.bot.getShardManager().getGuildById(giveaway.serverId());
        if (guild == null) {
            return null;
        }
        TextChannel channel = guild.getTextChannelById(giveaway.channelId());
        if (channel == null) {
            return null;
        }
        Message cachedMessage = channel.getHistory().getMessageById(giveaway.messageId());
        if (cachedMessage != null) {
            return cachedMessage;
        }
        try {
            return channel.retrieveMessageById(giveaway.messageId()).complete(true);
        } catch (CompletionException | ErrorResponseException ignored) {
            return null;
        }
    }
}
