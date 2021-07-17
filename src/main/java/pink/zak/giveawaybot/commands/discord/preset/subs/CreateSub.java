package pink.zak.giveawaybot.commands.discord.preset.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Preset;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

import java.util.List;

public class CreateSub extends SubCommand {

    public CreateSub(GiveawayBot bot) {
        super(bot, "create", true, false);
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        String presetName = event.getOption("presetname").getAsString();
        if (presetName.equalsIgnoreCase("default")) {
            this.langFor(server, Text.PRESET_CREATE_RESTRICTED_NAME, replacer -> replacer.set("name", "default")).to(event);
            return;
        }
        if (server.getPresets().size() >= (server.isPremium() ? 10000 : 5)) {
            this.langFor(server, server.isPremium() ? Text.PRESET_CREATE_LIMIT_REACHED_PREMIUM : Text.PRESET_CREATE_LIMIT_REACHED).to(event);
            return;
        }
        if (server.getPreset(presetName) != null) {
            this.langFor(server, Text.PRESET_CREATE_ALREADY_CALLED, replacer -> replacer.set("name", presetName)).to(event);
            return;
        }
        if (presetName.length() > 20) {
            this.langFor(server, Text.PRESET_CREATE_NAME_TOO_LONG).to(event);
            return;
        }
        this.langFor(server, Text.PRESET_CREATED).to(event);
        server.addPreset(new Preset(presetName));
    }
}
