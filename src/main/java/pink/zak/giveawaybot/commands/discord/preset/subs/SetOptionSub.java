package pink.zak.giveawaybot.commands.discord.preset.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Preset;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

public class SetOptionSub extends SubCommand {

    public SetOptionSub(GiveawayBot bot) {
        super(bot, "set", true, false);
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        String presetName = event.getOption("presetname").getAsString();
        Setting setting = Setting.valueOf(event.getOption("setting").getAsString());
        String inputValue = event.getOption("value").getAsString();
        if (presetName.equalsIgnoreCase("default")) {
            this.langFor(server, Text.PRESET_CANNOT_MODIFY_DEFAULT).to(event.getChannel());
            return;
        }
        Preset preset = server.getPreset(presetName);
        if (preset == null) {
            this.langFor(server, Text.COULDNT_FIND_PRESET).to(event.getChannel());
            return;
        }
        if (!setting.checkInputAny(inputValue, event.getGuild())) {
            this.langFor(server, Text.PRESET_SETTING_INCORRECT_INPUT, replacer -> replacer.set("setting", setting.getName())).to(event);
            return;
        }
        Object parsedValue = setting.parseAny(inputValue, event.getGuild());
        if (!setting.checkLimit(server, parsedValue)) {
            this.langFor(server, setting.getLimitMessage(), replacer -> replacer.set("max", server.isPremium() ? setting.getMaxPremiumValue() : setting.getMaxValue())).to(event);
            return;
        }
        preset.setSetting(setting, parsedValue);
        this.langFor(server, Text.PRESET_SETTING_SET, replacer -> replacer.set("setting", setting.getName()).set("value", parsedValue)).to(event);
    }
}
