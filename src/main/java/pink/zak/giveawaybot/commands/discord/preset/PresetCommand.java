package pink.zak.giveawaybot.commands.discord.preset;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.discord.preset.subs.CreateSub;
import pink.zak.giveawaybot.commands.discord.preset.subs.DeleteSub;
import pink.zak.giveawaybot.commands.discord.preset.subs.InfoSub;
import pink.zak.giveawaybot.commands.discord.preset.subs.ListSub;
import pink.zak.giveawaybot.commands.discord.preset.subs.OptionsSub;
import pink.zak.giveawaybot.commands.discord.preset.subs.SetOptionSub;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PresetCommand extends SimpleCommand {

    public PresetCommand(GiveawayBot bot) {
        super(bot, "preset", true, false);
//        ImportCmdUtils cmdUtils = new ImportCmdUtils(bot);

        this.setSubCommands(
//            new ExportAllSub(bot),
//            new ExportSub(bot),
//            new ImportSub(bot, cmdUtils),
            new CreateSub(bot),
            new DeleteSub(bot),
            new ListSub(bot),
            new OptionsSub(bot),
            new InfoSub(bot),
            new SetOptionSub(bot)
        );
    }

    @Override
    protected CommandData createCommandData() {
        return new CommandData("preset", "Preset related commands")
            .addSubcommands(
                new SubcommandData("create", "Create a preset")
                    .addOption(OptionType.STRING, "presetname", "The name of the preset", true),
                new SubcommandData("delete", "Delete a preset")
                    .addOption(OptionType.STRING, "presetname", "The name of the preset", true),
                new SubcommandData("info", "Get info about a preset")
                    .addOption(OptionType.STRING, "presetname", "The name of the preset", true),
                new SubcommandData("list", "List presets"),
                new SubcommandData("options", "List preset options"),
                new SubcommandData("set", "Set a setting of a preset")
                    .addOption(OptionType.STRING, "presetname", "The name of the preset", true)
                    .addOptions(
                        new OptionData(OptionType.STRING, "setting", "The setting to change", true)
                            .addChoices(
                                Arrays.stream(Setting.values())
                                    .map(setting -> new Command.Choice(setting.getName(), setting.toString()))
                                    .collect(Collectors.toSet())
                            )
                    )
                    .addOption(OptionType.STRING, "value", "The value of the setting", true)
            );
    }
}
