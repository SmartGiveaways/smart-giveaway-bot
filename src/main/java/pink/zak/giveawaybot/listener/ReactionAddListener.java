package pink.zak.giveawaybot.listener;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.Defaults;
import pink.zak.giveawaybot.data.cache.GiveawayCache;
import pink.zak.giveawaybot.data.cache.ServerCache;
import pink.zak.giveawaybot.data.models.Preset;
import pink.zak.giveawaybot.data.models.User;
import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.pipelines.entries.EntryType;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.types.ReactionContainer;
import pink.zak.giveawaybot.threads.ThreadFunction;

import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ReactionAddListener extends ListenerAdapter {
    private final ServerCache serverCache;
    private final GiveawayCache giveawayCache;
    private final Preset defaultPreset;
    private final AtomicInteger entryCount;
    private final EnumMap<EntryType, AtomicInteger> baseMap;

    public ReactionAddListener(GiveawayBot bot) {
        this.serverCache = bot.getServerCache();
        this.giveawayCache = bot.getGiveawayCache();
        this.defaultPreset = Defaults.defaultPreset;
        this.entryCount = bot.getMetricsLogger().getGenericMetrics().getEntryCount();

        this.baseMap = Maps.newEnumMap(EntryType.class);
        this.baseMap.put(EntryType.REACTION, new AtomicInteger(1));

    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        long messageId = event.getMessageIdLong();
        long userId = event.getUserIdLong();
        this.serverCache.getAsync(event.getGuild().getIdLong(), ThreadFunction.GENERAL).thenAccept(server -> {
            if (!server.getActiveGiveaways().contains(messageId)) {
                return;
            }
            CurrentGiveaway giveaway = this.giveawayCache.get(messageId);
            if (giveaway.getEnteredUsers().contains(userId)) {
                return;
            }
            Preset preset = giveaway.getPresetName().equals("default") ? this.defaultPreset : server.getPreset(giveaway.getPresetName());
            MessageReaction.ReactionEmote messageReaction = event.getReactionEmote();
            MessageReaction.ReactionEmote setReaction = ((ReactionContainer) preset.getSetting(Setting.REACT_TO_ENTER_EMOJI)).getReactionEmote();
            if (messageReaction.isEmoji() != setReaction.isEmoji()
                    || (messageReaction.isEmoji() && !messageReaction.getAsCodepoints().equals(setReaction.getAsCodepoints()))
                    || (messageReaction.isEmote() && !messageReaction.getAsReactionCode().equals(setReaction.getAsReactionCode()))) {
                return;
            }
            User user = server.getUserCache().get(userId);
            giveaway.getEnteredUsers().add(userId);
            user.getEntries().put(giveaway.getMessageId(), this.baseMap.clone());
            this.entryCount.incrementAndGet();
        }).exceptionally(ex -> {
            JdaBot.logger.error("Error in ReactionAddListener point A", ex);
            return null;
        });
    }
}
