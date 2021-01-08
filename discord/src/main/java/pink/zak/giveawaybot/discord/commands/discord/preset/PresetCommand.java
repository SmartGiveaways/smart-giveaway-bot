package pink.zak.giveawaybot.discord.commands.discord.preset;

import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.commands.discord.preset.subs.*;
import pink.zak.giveawaybot.discord.commands.discord.preset.subs.exports.ExportAllSub;
import pink.zak.giveawaybot.discord.commands.discord.preset.subs.exports.ImportSub;
import pink.zak.giveawaybot.discord.commands.discord.preset.subs.exports.utils.ImportCmdUtils;
import pink.zak.giveawaybot.discord.lang.enums.Text;
import pink.zak.giveawaybot.discord.commands.discord.preset.subs.exports.ExportSub;
import pink.zak.giveawaybot.discord.service.command.discord.command.SimpleHelpCommand;

public class PresetCommand extends SimpleHelpCommand {

    public PresetCommand(GiveawayBot bot) {
        super(bot, "preset", true, false);
        ImportCmdUtils cmdUtils = new ImportCmdUtils(bot);

        this.setAliases("presets");
        this.setSubCommands(
                new ExportAllSub(bot),
                new ExportSub(bot),
                new ImportSub(bot, cmdUtils),
                new CreateSub(bot),
                new DeleteSub(bot),
                new ListSub(bot),
                new OptionsSub(bot),
                new PresetOptionsSub(bot),
                new SetOptionSub(bot)
        );

        this.setupMessages(Text.PRESET_EMBED_TITLE, Text.PRESET_EMBED_CONTENT);
    }
}
