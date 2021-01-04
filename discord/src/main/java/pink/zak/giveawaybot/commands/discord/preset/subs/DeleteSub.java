package pink.zak.giveawaybot.commands.discord.preset.subs;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

import java.util.List;
import java.util.UUID;

public class DeleteSub extends SubCommand {
    private final GiveawayCache giveawayCache;
    private final ScheduledGiveawayCache scheduledGiveawayCache;

    public DeleteSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.giveawayCache = bot.getGiveawayCache();
        this.scheduledGiveawayCache = bot.getScheduledGiveawayCache();

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
        for (long id : server.getActiveGiveaways()) {
            if (this.giveawayCache.getSync(id).getPresetName().equals(presetName)) {
                return false;
            }
        }
        for (UUID uuid : server.getScheduledGiveaways()) {
            if (this.scheduledGiveawayCache.getSync(uuid).getPresetName().equals(presetName)) {
                return false;
            }
        }
        return true;
    }
}
