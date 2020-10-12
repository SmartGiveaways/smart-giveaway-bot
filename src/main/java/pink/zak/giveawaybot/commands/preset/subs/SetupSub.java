package pink.zak.giveawaybot.commands.preset.subs;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.enums.PresetSetupStage;
import pink.zak.giveawaybot.service.cache.Cache;
import pink.zak.giveawaybot.service.cache.CacheBuilder;
import pink.zak.giveawaybot.service.cache.options.CacheExpiryListener;
import pink.zak.giveawaybot.service.command.command.SubCommand;
import pink.zak.giveawaybot.service.tuple.MutablePair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SetupSub extends SubCommand implements CacheExpiryListener<Long, MutablePair<Long, PresetSetupStage>>, EventListener {
    private final Cache<Long, MutablePair<Long, PresetSetupStage>> serverUsersInSetup;
    private final Map<Long, Long> activeGuildChannels = Maps.newConcurrentMap();

    public SetupSub(GiveawayBot bot) {
        super(bot);
        this.serverUsersInSetup = new CacheBuilder<Long, MutablePair<Long, PresetSetupStage>>()
                .setControlling(bot)
                .expireAfterAccess(1, TimeUnit.MINUTES).build();;

        this.addFlat("setup");
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        long guildId = event.getGuild().getIdLong();
        long senderId = sender.getIdLong();
        if (!this.serverUsersInSetup.contains(guildId)) {
            this.serverUsersInSetup.set(guildId, MutablePair.of(senderId, PresetSetupStage.SETTING_NAME));
            this.activeGuildChannels.put(guildId, event.getChannel().getIdLong());
            event.getChannel().sendMessage("Starting setup. What would you like to call your preset?").queue();
            return;
        }
        this.serverUsersInSetup.get(guildId).whenComplete((longPresetSetupStageMutablePair, throwable) -> {
            if (!(longPresetSetupStageMutablePair.getKey() == senderId)) {
                event.getChannel().sendMessage(":x: Only one person in a discord can use the setup command at once. You can always do it individually.").queue();
            } else {
                event.getChannel().sendMessage(":white_check_mark: Clearing your setup and starting over.").queue();
                this.activeGuildChannels.remove(guildId);
                this.serverUsersInSetup.invalidate(guildId, false);
                this.onExecute(sender, event, args);
            }
        });
    }

    @Override
    public void onExpiry(Long key, MutablePair<Long, PresetSetupStage> value) {
        this.activeGuildChannels.remove(key);
    }

    @SneakyThrows
    public void onMessageReceived(MessageReceivedEvent event) {
        long guildId = event.getGuild().getIdLong();
        long senderId = event.getAuthor().getIdLong();
        long channelId = event.getTextChannel().getIdLong();
        if (!this.serverUsersInSetup.contains(guildId) || channelId != this.activeGuildChannels.get(guildId) || this.serverUsersInSetup.get(guildId).get().getKey() != senderId) {
            return;
        }

    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            this.onMessageReceived((MessageReceivedEvent) event);
        }
    }
}
