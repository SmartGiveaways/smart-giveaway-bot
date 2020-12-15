package pink.zak.giveawaybot.commands.preset.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public class SetOptionSub extends SubCommand {

    public SetOptionSub(GiveawayBot bot) {
        super(bot, true, false, false);

        this.addFlat("set");
        this.addArgument(String.class); // preset name
        this.addArgument(String.class);  // Setting name
        this.addArgument(String.class); // Value
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        String presetName = this.parseArgument(args, event.getGuild(), 1);
        if (presetName.equalsIgnoreCase("default")) {
            this.langFor(server, Text.PRESET_CANNOT_MODIFY_DEFAULT).to(event.getChannel());
            return;
        }
        Preset preset = server.getPreset(presetName);
        if (preset == null) {
            this.langFor(server, Text.COULDNT_FIND_PRESET).to(event.getChannel());
            return;
        }
        String settingName = this.parseArgument(args, event.getGuild(), 2);
        Setting setting = Setting.match(settingName);
        if (setting == null) {
            this.langFor(server, Text.COULDNT_FIND_SETTING).to(event.getChannel());
            return;
        }
        String inputValue = this.parseArgument(args, event.getGuild(), 3);
        if (!setting.checkInputAny(inputValue, event.getGuild())) {
            this.langFor(server, Text.PRESET_SETTING_INCORRECT_INPUT, replacer -> replacer.set("setting", setting.getPrimaryConfigName())).to(event.getChannel());
            return;
        }
        Object parsedValue = setting.parseAny(inputValue, event.getGuild());
        if (!setting.checkLimit(server, parsedValue)) {
            this.langFor(server, setting.getLimitMessage(), replacer -> replacer.set("max", server.isPremium() ? setting.getMaxPremiumValue() : setting.getMaxValue())).to(event.getChannel());
            return;
        }
        preset.setSetting(setting, parsedValue);
        this.langFor(server, Text.PRESET_SETTING_SET, replacer -> replacer.set("setting", setting.getPrimaryConfigName()).set("value", parsedValue)).to(event.getChannel());
    }
}
