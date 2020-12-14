package pink.zak.giveawaybot.commands.preset;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.preset.subs.*;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.service.command.command.SimpleHelpCommand;

public class PresetCommand extends SimpleHelpCommand {

    public PresetCommand(GiveawayBot bot) {
        super(bot, "preset", true, false);

        this.setAliases("presets");
        this.setSubCommands(
                new CreateSub(bot),
                new DeleteSub(bot),
                new ListSub(bot),
                new OptionsSub(bot),
                new PresetOptionsSub(bot),
                new SetOptionSub(bot)
        );

        this.buildMessages(Text.PRESET_EMBED_TITLE, Text.PRESET_EMBED_CONTENT);
    }
}
