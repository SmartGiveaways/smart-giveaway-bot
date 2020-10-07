package pink.zak.giveawaybot.commands.preset.subs;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.UUID;

public class DeleteSub extends SubCommand {
    private final GiveawayCache giveawayCache;
    private final ServerCache serverCache;

    public DeleteSub(GiveawayBot bot) {
        super(bot);
        this.giveawayCache = bot.getGiveawayCache();
        this.serverCache = bot.getServerCache();

        this.addFlatWithAliases("delete", "remove");
        this.addArgument(String.class); // Preset name
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, String[] args) {
        this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
            String presetName = this.parseArgument(args, event.getGuild(), 1);
            if (!server.getPresets().containsKey(presetName)) {
                String message = ":x: I can only delete presets that exist.";
                if (server.getPresets().size() > 0) {
                    message = message + " Here are your presets: " + String.join(", ", server.getPresets().keySet()) + ".";
                }
                event.getChannel().sendMessage(message).queue();
                return;
            }
            String lowerPresetName = presetName.toLowerCase();
            if (!this.canBeDeleted(server, lowerPresetName)) {
                event.getChannel().sendMessage("That preset is being used in a giveaway right now. It must not be in use for you to delete it.").queue();
                return;
            }
            server.getPresets().remove(lowerPresetName);
            event.getChannel().sendMessage("Deleted the `" + presetName + "` preset.").queue();
        });
    }

    @SneakyThrows
    private boolean canBeDeleted(Server server, String presetName) {
        for (UUID giveawayUuid : server.getActiveGiveaways().values()) {
            if (this.giveawayCache.get(giveawayUuid).thenApply(giveaway -> giveaway.presetName().equals(presetName)).get()) {
                return false;
            }
        }
        return true;
    }
}
