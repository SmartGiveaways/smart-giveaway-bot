package pink.zak.giveawaybot.commands.discord.preset.subs;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.cache.GiveawayCache;
import pink.zak.giveawaybot.data.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.command.discord.command.BotSubCommand;

import java.util.UUID;

public class DeleteSub extends BotSubCommand {
    private final GiveawayCache giveawayCache;
    private final ScheduledGiveawayCache scheduledGiveawayCache;

    public DeleteSub(GiveawayBot bot) {
        super(bot, "delete", false, false);
        this.giveawayCache = bot.getGiveawayCache();
        this.scheduledGiveawayCache = bot.getScheduledGiveawayCache();
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        String presetName = event.getOption("presetname").getAsString();
        if (!server.getPresets().containsKey(presetName)) {
            String message = this.langFor(server, Text.COULDNT_FIND_PRESET).toString();
            if (server.getPresets().size() > 0) {
                message = message + this.langFor(server, Text.PRESET_DELETE_SHOW_PRESETS_ADDON, replacer -> replacer.set("preset-list", String.join(", ", server.getPresets().keySet())));
            }
            event.reply(message).setEphemeral(true).queue();
            return;
        }
        String lowerPresetName = presetName.toLowerCase();
        if (!this.canBeDeleted(server, lowerPresetName)) {
            this.langFor(server, Text.PRESET_DELETE_IN_USE).to(event, true);
            return;
        }
        server.getPresets().remove(lowerPresetName);
        this.langFor(server, Text.PRESET_DELETED, replacer -> replacer.set("preset", presetName)).to(event, true);
    }

    @SneakyThrows
    private boolean canBeDeleted(Server server, String presetName) {
        for (long id : server.getActiveGiveaways()) {
            if (this.giveawayCache.get(id).getPresetName().equals(presetName)) {
                return false;
            }
        }
        for (UUID uuid : server.getScheduledGiveaways()) {
            if (this.scheduledGiveawayCache.get(uuid).getPresetName().equals(presetName)) {
                return false;
            }
        }
        return true;
    }
}
