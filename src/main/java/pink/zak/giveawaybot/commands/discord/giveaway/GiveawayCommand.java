package pink.zak.giveawaybot.commands.discord.giveaway;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.CreateSub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.DeleteSub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.HistorySub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.InfoSub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.ListScheduledSub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.ListSub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.RerollSub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.ScheduleSub;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;

public class GiveawayCommand extends SimpleCommand {

    public GiveawayCommand(GiveawayBot bot) {
        super(bot, "giveaway", true, false);
        GiveawayCmdUtils cmdUtils = new GiveawayCmdUtils(bot);

        this.setSubCommands(
            new CreateSub(bot, cmdUtils),
            new DeleteSub(bot),
            new HistorySub(bot),
            new InfoSub(bot),
            new ListScheduledSub(bot),
            new ListSub(bot),
            new RerollSub(bot),
            new ScheduleSub(bot, cmdUtils)
        );
    }

    @Override
    protected CommandData createCommandData() {
        return new CommandData("giveaway", "SmartGiveaways command")
            .addSubcommands(
                new SubcommandData("create", "Create a giveaway")
                    .addOption(OptionType.STRING, "length", "The length of the giveaway, e.g 7d or 1h", true)
                    .addOption(OptionType.INTEGER, "winners", "The amount of winners of the giveaway", true)
                    .addOption(OptionType.STRING, "name", "The name of the giveaway", true)
                    .addOption(OptionType.STRING, "presetname", "The name of the preset to use. Will use default if not specified.", false)
                    .addOption(OptionType.CHANNEL, "channel", "The channel to host the giveaway in. Will use this channel if not specified.", false),
                new SubcommandData("info", "Get info about a giveaway")
                    .addOption(OptionType.STRING, "giveawayid", "The ID of the giveaway or expired giveaway", true),
                new SubcommandData("schedule", "Schedule a giveaway")
                    .addOption(OptionType.STRING, "delay", "The time until the giveaway begins, e.g 7d or 1h", true)
                    .addOption(OptionType.STRING, "length", "The length of the giveaway, e.g 7d or 1h", true)
                    .addOption(OptionType.INTEGER, "winners", "The amount of winners of the giveaway", true)
                    .addOption(OptionType.STRING, "name", "The name of the giveaway", true)
                    .addOption(OptionType.STRING, "presetname", "The name of the preset to use. Will use default if not specified.", false)
                    .addOption(OptionType.CHANNEL, "channel", "The channel to host the giveaway in. Will use this channel if not specified.", false),
                new SubcommandData("delete", "Delete a giveaway")
                    .addOption(OptionType.STRING, "giveawayid", "The ID of the giveaway", true),
                new SubcommandData("history", "Get giveaway history"),
                new SubcommandData("reroll", "Reroll a giveaway's winners")
                    .addOption(OptionType.INTEGER, "giveawayid", "The ID of the giveaway", true)
            )
            .addSubcommandGroups(
                new SubcommandGroupData("list", "List giveaways")
                    .addSubcommands(
                        new SubcommandData("current", "List current giveaways"),
                        new SubcommandData("scheduled", "List scheduled giveaways")
                    )
            );
    }
}
