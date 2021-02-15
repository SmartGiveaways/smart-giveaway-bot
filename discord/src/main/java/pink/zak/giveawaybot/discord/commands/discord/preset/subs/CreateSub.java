package pink.zak.giveawaybot.discord.commands.discord.preset.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.models.Preset;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.lang.Text;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;

import java.util.List;

public class CreateSub extends SubCommand {

    public CreateSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.addFlat("create");
        this.addArgument(String.class);
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        String name = this.parseArgument(args, event.getGuild(), 1);
        if (name.equalsIgnoreCase("default")) {
            this.langFor(server, Text.PRESET_CREATE_RESTRICTED_NAME, replacer -> replacer.set("name", "default")).to(event.getChannel());
            return;
        }
        if (server.getPresets().size() >= (server.isPremium() ? 10000 : 5)) {
            this.langFor(server, server.isPremium() ? Text.PRESET_CREATE_LIMIT_REACHED_PREMIUM : Text.PRESET_CREATE_LIMIT_REACHED).to(event.getChannel());
            return;
        }
        if (server.getPreset(name) != null) {
            this.langFor(server, Text.PRESET_CREATE_ALREADY_CALLED, replacer -> replacer.set("name", name)).to(event.getChannel());
            return;
        }
        if (name.length() > 20) {
            this.langFor(server, Text.PRESET_CREATE_NAME_TOO_LONG).to(event.getChannel());
            return;
        }
        this.langFor(server, Text.PRESET_CREATED).to(event.getChannel());
        server.addPreset(new Preset(name));
    }
}
