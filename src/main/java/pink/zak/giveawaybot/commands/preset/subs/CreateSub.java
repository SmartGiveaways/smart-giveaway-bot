package pink.zak.giveawaybot.commands.preset.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public class CreateSub extends SubCommand {

    public CreateSub(GiveawayBot bot) {
        super(bot);
        this.addFlat("create");
        this.addArgument(String.class);
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        String name = this.parseArgument(args, event.getGuild(), 1);
        if (name.equalsIgnoreCase("default")) {
            this.langFor(server, Text.PRESET_CREATE_RESTRICTED_NAME, replacer -> replacer.set("name", "default")).to(event.getTextChannel());
            return;
        }
        if (server.getPresets().size() >= (server.isPremium() ? 10 : 5)) {
            this.langFor(server, server.isPremium() ? Text.PRESET_CREATE_LIMIT_REACHED_PREMIUM : Text.PRESET_CREATE_LIMIT_REACHED).to(event.getTextChannel());
            return;
        }
        if (server.getPreset(name) != null) {
            this.langFor(server, Text.PRESET_CREATE_ALREADY_CALLED, replacer -> replacer.set("name", name)).to(event.getTextChannel());
            return;
        }
        if (name.length() > 20) {
            this.langFor(server, Text.PRESET_CREATE_NAME_TOO_LONG).to(event.getTextChannel());
            return;
        }
        if (name.length() < 4) {
            this.langFor(server, Text.PRESET_CREATE_NAME_TOO_SHORT).to(event.getTextChannel());
            return;
        }
        this.langFor(server, Text.PRESET_CREATED).to(event.getTextChannel());
        server.addPreset(new Preset(name));
    }
}
