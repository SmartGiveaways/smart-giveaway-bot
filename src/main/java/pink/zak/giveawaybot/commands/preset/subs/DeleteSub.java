package pink.zak.giveawaybot.commands.preset.subs;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public class DeleteSub extends SubCommand {
    private final GiveawayCache giveawayCache;

    public DeleteSub(GiveawayBot bot) {
        super(bot);
        this.giveawayCache = bot.getGiveawayCache();

        this.addFlatWithAliases("delete", "remove");
        this.addArgument(String.class); // Preset name
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
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
    }

    @SneakyThrows
    private boolean canBeDeleted(Server server, String presetName) {
        for (long giveawayUuid : server.getActiveGiveaways()) {
            if (this.giveawayCache.get(giveawayUuid).thenApply(giveaway -> giveaway.presetName().equals(presetName)).get()) {
                return false;
            }
        }
        return true;
    }
}
