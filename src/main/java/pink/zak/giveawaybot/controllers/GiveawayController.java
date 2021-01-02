package pink.zak.giveawaybot.controllers;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.defaults.Defaults;
import pink.zak.giveawaybot.enums.ReturnCode;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.metrics.helpers.LatencyMonitor;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.models.giveaway.Giveaway;
import pink.zak.giveawaybot.models.giveaway.RichGiveaway;
import pink.zak.giveawaybot.pipelines.giveaway.GiveawayPipeline;
import pink.zak.giveawaybot.pipelines.giveaway.steps.DeletionStep;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.time.Time;
import pink.zak.giveawaybot.service.time.TimeIdentifier;
import pink.zak.giveawaybot.service.tuple.ImmutablePair;
import pink.zak.giveawaybot.service.types.ReactionContainer;
import pink.zak.giveawaybot.storage.GiveawayStorage;
import pink.zak.giveawaybot.threads.ThreadFunction;
import pink.zak.giveawaybot.threads.ThreadManager;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GiveawayController {
    private final Map<CurrentGiveaway, ScheduledFuture<?>> scheduledFutures = Maps.newConcurrentMap();
    private final ThreadManager threadManager;
    private final LanguageRegistry languageRegistry;
    private final GiveawayCache giveawayCache;
    private final GiveawayStorage giveawayStorage;
    private final ScheduledGiveawayCache scheduledGiveawayCache;
    private final ServerCache serverCache;
    private final Preset defaultPreset;
    private final Palette palette;
    private final Defaults defaults;
    private final GiveawayBot bot;

    private final GiveawayPipeline giveawayPipeline;
    private final DeletionStep deletionStep;

    public GiveawayController(GiveawayBot bot) {
        this.threadManager = bot.getThreadManager();
        this.languageRegistry = bot.getLanguageRegistry();
        this.giveawayCache = bot.getGiveawayCache();
        this.giveawayStorage = bot.getGiveawayStorage();
        this.scheduledGiveawayCache = bot.getScheduledGiveawayCache();
        this.serverCache = bot.getServerCache();
        this.defaultPreset = bot.getDefaults().getDefaultPreset();
        this.palette = bot.getDefaults().getPalette();
        this.defaults = bot.getDefaults();
        this.bot = bot;

        this.giveawayPipeline = new GiveawayPipeline(bot, this);
        this.deletionStep = new DeletionStep(bot, this);

        this.loadAllGiveaways();
        this.startGiveawayUpdater();
    }

    public ImmutablePair<CurrentGiveaway, ReturnCode> createGiveaway(Server server, long length, long endTime, int winnerAmount, TextChannel giveawayChannel, String presetName, String giveawayItem) {
        if (server.activeGiveaways().size() >= (server.isPremium() ? 10 : 5)) {
            return ImmutablePair.of(null, ReturnCode.GIVEAWAY_LIMIT_FAILURE);
        }
        if (!giveawayChannel.getGuild().getSelfMember().hasPermission(giveawayChannel, this.defaults.getRequiredPermissions())) {
            return ImmutablePair.of(null, ReturnCode.PERMISSIONS_FAILURE);
        }
        Preset preset = presetName.equalsIgnoreCase("default") ? this.defaultPreset : server.preset(presetName);
        if (preset == null) {
            return ImmutablePair.of(null, ReturnCode.NO_PRESET);
        }
        boolean reactToEnter = preset.getSetting(Setting.ENABLE_REACT_TO_ENTER);
        try {
            Message message = giveawayChannel.sendMessage(new EmbedBuilder()
                    .setTitle(this.languageRegistry.get(server, Text.GIVEAWAY_EMBED_TITLE, replacer -> replacer.set("item", giveawayItem)).get())
                    .setDescription(this.languageRegistry.get(server, reactToEnter ? Text.GIVEAWAY_EMBED_DESCRIPTION_REACTION : Text.GIVEAWAY_EMBED_DESCRIPTION_ALL).get())
                    .setColor(this.palette.primary())
                    .setFooter(this.getFooter(server, length, winnerAmount))
                    .build()).complete(true);

            CurrentGiveaway giveaway = new CurrentGiveaway(message.getIdLong(), giveawayChannel.getIdLong(), giveawayChannel.getGuild().getIdLong(), endTime, winnerAmount, presetName, giveawayItem);

            // Add reaction
            if (reactToEnter) {
                MessageReaction.ReactionEmote reaction = ((ReactionContainer) preset.getSetting(Setting.REACT_TO_ENTER_EMOJI)).getReactionEmote();
                if (reaction == null) {
                    return ImmutablePair.of(giveaway, ReturnCode.UNKNOWN_EMOJI);
                }
                if (reaction.isEmoji()) {
                    message.addReaction(reaction.getEmoji()).queue();
                } else {
                    try {
                        message.addReaction(reaction.getEmote()).queue();
                    } catch (ErrorResponseException ex) {
                        if (ex.getErrorResponse() == ErrorResponse.UNKNOWN_EMOJI) {
                            return ImmutablePair.of(giveaway, ReturnCode.UNKNOWN_EMOJI);
                        }
                    }
                }
            }
            this.giveawayCache.addGiveaway(giveaway);
            this.giveawayStorage.save(giveaway);
            server.addActiveGiveaway(giveaway);
            this.startGiveawayTimer(giveaway);
            return ImmutablePair.of(giveaway, ReturnCode.SUCCESS);
        } catch (RateLimitedException ex) {
            GiveawayBot.logger().error("", ex);
            return ImmutablePair.of(null, ReturnCode.RATE_LIMIT_FAILURE);
        } catch (InsufficientPermissionException ex) {
            return ImmutablePair.of(null, ReturnCode.PERMISSIONS_FAILURE);
        }
    }

    public Set<Giveaway> getCurrentAndScheduledGiveaways(Server server) {
        Set<Giveaway> giveaways = Sets.newHashSet();
        for (long id : server.activeGiveaways()) {
            giveaways.add(this.giveawayCache.getSync(id));
        }
        for (UUID id : server.scheduledGiveaways()) {
            giveaways.add(this.scheduledGiveawayCache.getSync(id));
        }
        return giveaways;
    }

    /**
     * Gets the most giveaways concurrently active in a certain period
     * Surprisingly performance efficient (sub 1ms when giveaways are cached)
     */
    public int getGiveawayCountAt(Server server, long startTime, long endTime) {
        Set<Long> checkpoints = Sets.newHashSet(startTime, endTime);
        Set<Giveaway> giveaways = this.getCurrentAndScheduledGiveaways(server);
        for (Giveaway giveaway : giveaways) {
            if (giveaway.startTime() >= startTime && giveaway.startTime() <= endTime) {
                checkpoints.add(giveaway.startTime());
            }
            if (giveaway.endTime() <= endTime && giveaway.endTime() >= startTime) {
                checkpoints.add(giveaway.endTime());
            }
        }
        int maxCount = 0;
        for (long checkpoint : checkpoints) {
            int checkpointCount = 0;
            for (Giveaway giveaway : giveaways) {
                if (giveaway.startTime() <= checkpoint && giveaway.endTime() >= checkpoint) {
                    checkpointCount++;
                }
            }
            if (checkpointCount > maxCount) {
                maxCount = checkpointCount;
            }
        }
        return maxCount;
    }

    public void loadAllGiveaways() {
        long loadStartTime = System.currentTimeMillis();
        this.bot.runAsync(ThreadFunction.STORAGE, () -> {
            for (CurrentGiveaway giveaway : this.giveawayStorage.loadAll()) {
                if (this.getGiveawayMessage(giveaway) == null) {
                    this.deletionStep.delete(giveaway);
                    continue;
                }
                if (!giveaway.isActive()) {
                    this.giveawayPipeline.endGiveaway(giveaway);
                    continue;
                }
                this.giveawayCache.addGiveaway(giveaway);
                this.startGiveawayTimer(giveaway);
            }
            return null;
        }).whenComplete((o, ex) -> {
            if (ex != null) {
                GiveawayBot.logger().error("", ex);
            }
            GiveawayBot.logger().info("Loaded {} giveaways in {} milliseconds", this.giveawayCache.size(), System.currentTimeMillis() - loadStartTime);
        });
    }

    private void startGiveawayUpdater() {
        AtomicInteger counter = new AtomicInteger();
        LatencyMonitor latencyMonitor = this.bot.getLatencyMonitor();
        this.threadManager.getScheduler().scheduleAtFixedRate(() -> {
            if (!latencyMonitor.isLatencyUsable()) {
                GiveawayBot.logger().warn("Latency was not usable so did not update giveaways ({}ms)", latencyMonitor.getLastTiming());
                return;
            }
            counter.getAndIncrement();
            for (CurrentGiveaway giveaway : this.giveawayCache.getMap().values()) {
                if (!giveaway.isActive()) {
                    return;
                }
                if (!latencyMonitor.isLatencyUsable() || !this.shouldUpdateMessage(counter, giveaway)) {
                    return;
                }
                this.serverCache.get(giveaway.serverId()).thenAccept(server -> {
                    Message message = this.getGiveawayMessage(giveaway);
                    if (message == null) {
                        GiveawayBot.logger().warn("Giveaway did not delete correctly or the discord api is dying ({} in server {}).", giveaway.messageId(), giveaway.serverId());
                        return;
                    }
                    Preset preset = giveaway.presetName().equals("default") ? this.defaultPreset : server.preset(giveaway.presetName());
                    boolean reactToEnter = preset.getSetting(Setting.ENABLE_REACT_TO_ENTER);
                    message.editMessage(new EmbedBuilder()
                            .setTitle(this.languageRegistry.get(server, Text.GIVEAWAY_EMBED_TITLE, replacer -> replacer.set("item", giveaway.giveawayItem())).get())
                            .setDescription(this.languageRegistry.get(server, reactToEnter ? Text.GIVEAWAY_EMBED_DESCRIPTION_REACTION : Text.GIVEAWAY_EMBED_DESCRIPTION_ALL).get())
                            .setColor(this.palette.primary())
                            .setFooter(this.getFooter(server, giveaway.timeToExpiry(), giveaway.winnerAmount()))
                            .build()).queue();
                }).exceptionally(ex -> {
                    GiveawayBot.logger().error("Error in giveaway updater: ", ex);
                    return null;
                });

            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    private boolean shouldUpdateMessage(AtomicInteger counter, CurrentGiveaway giveaway) {
        int count = counter.get();
        long timeToExpiry = giveaway.timeToExpiry();
        if (timeToExpiry >= TimeIdentifier.WEEK.getMilliseconds()) {
            return count % 2880 == 0;
        }
        if (timeToExpiry >= TimeIdentifier.DAY.getMilliseconds()) {
            return count % 480 == 0;
        }
        if (timeToExpiry >= TimeIdentifier.HOUR.getMilliseconds()) {
            return count % 30 == 0;
        }
        if (timeToExpiry >= TimeIdentifier.MINUTE.getMilliseconds() * 30) {
            return count % 10 == 0;
        }
        return true;
    }

    private void startGiveawayTimer(CurrentGiveaway giveaway) {
        this.scheduledFutures.put(giveaway, this.threadManager.getScheduler().schedule(() -> {
            GiveawayBot.logger().debug("Giveaway {} expired", giveaway.messageId());
            this.giveawayPipeline.endGiveaway(giveaway);
        }, giveaway.timeToExpiry(), TimeUnit.MILLISECONDS));
    }

    @SneakyThrows
    public Message getGiveawayMessage(RichGiveaway giveaway) {
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

    private String getFooter(Server server, long length, int winnerAmount) {
        return this.languageRegistry.get(server, winnerAmount > 1 ? Text.GIVEAWAY_EMBED_FOOTER_PLURAL : Text.GIVEAWAY_EMBED_FOOTER_SINGULAR,
                replacer -> replacer.set("time", Time.format(length)).set("winner-count", winnerAmount)).get();
    }

    public Map<CurrentGiveaway, ScheduledFuture<?>> getScheduledFutures() {
        return this.scheduledFutures;
    }
}
