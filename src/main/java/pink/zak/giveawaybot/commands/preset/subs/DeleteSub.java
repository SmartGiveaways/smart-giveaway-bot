package pink.zak.giveawaybot.commands.preset.subs;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public class DeleteSub extends SubCommand {
    private final GiveawayCache giveawayCache;

    public DeleteSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.giveawayCache = bot.getGiveawayCache();

        this.addFlatWithAliases("delete", "remove");
        this.addArgument(String.class); // Preset name
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        String presetName = this.parseArgument(args, event.getGuild(), 1);
        if (!server.getPresets().containsKey(presetName)) {
            String message = this.langFor(server, Text.COULDNT_FIND_PRESET).get();
            if (server.getPresets().size() > 0) {
                message = message + this.langFor(server, Text.PRESET_DELETE_SHOW_PRESETS_ADDON, replacer -> replacer.set("preset-list", String.join(", ", server.getPresets().keySet())));
            }
            event.getChannel().sendMessage(message).queue();
            return;
        }
        String lowerPresetName = presetName.toLowerCase();
        if (!this.canBeDeleted(server, lowerPresetName)) {
            this.langFor(server, Text.PRESET_DELETE_IN_USE).to(event.getChannel());
            return;
        }
        server.getPresets().remove(lowerPresetName);
        this.langFor(server, Text.PRESET_DELETED, replacer -> replacer.set("preset", presetName)).to(event.getChannel());
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
