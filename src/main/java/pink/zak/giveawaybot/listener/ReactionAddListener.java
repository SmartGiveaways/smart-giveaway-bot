package pink.zak.giveawaybot.listener;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.enums.EntryType;

import java.util.EnumMap;
import java.util.UUID;

public class ReactionAddListener extends ListenerAdapter {
    private final ServerCache serverCache;
    private final GiveawayCache giveawayCache;
    private final EnumMap<EntryType, Integer> baseMap = Maps.newEnumMap(EntryType.class);

    public ReactionAddListener(GiveawayBot bot) {
        this.serverCache = bot.getServerCache();
        this.giveawayCache = bot.getGiveawayCache();

        this.baseMap.put(EntryType.REACTION, 1);
    }

    @SubscribeEvent
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot() || event.getReactionEmote().isEmote() || !event.getReactionEmote().getAsCodepoints().equals("U+1f389")) { // U+1f389 is the unicode for the expected reaction
            return;
        }
        this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
            long messageId = event.getMessageIdLong();
            UUID giveawayUuid = server.getActiveGiveaways().get(messageId);
            if (giveawayUuid == null) {
                return;
            }
            long userId = event.getUserIdLong();

            server.getUserCache().get(userId).thenAccept(user -> {
                if (!user.entries().containsKey(giveawayUuid)) {
                    user.entries().put(giveawayUuid, this.baseMap.clone());
                }
            });
            this.giveawayCache.get(server.getActiveGiveaways().get(messageId)).thenAccept(giveaway -> giveaway.enteredUsers().add(userId));
        });
    }
}
