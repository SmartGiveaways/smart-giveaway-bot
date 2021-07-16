package pink.zak.giveawaybot.commands.discord.admin;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.discord.admin.subs.language.ListLanguagesSub;
import pink.zak.giveawaybot.commands.discord.admin.subs.language.SetLanguageSub;
import pink.zak.giveawaybot.commands.discord.admin.subs.manager.ListManagersSub;
import pink.zak.giveawaybot.commands.discord.admin.subs.manager.ManagerAddSub;
import pink.zak.giveawaybot.commands.discord.admin.subs.manager.ManagerRemoveSub;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.command.discord.command.SimpleHelpCommand;

import java.util.stream.Collectors;

public class AdminCommand extends SimpleHelpCommand {

    public AdminCommand(GiveawayBot bot) {
        super(bot, "gadmin", true, false);

        this.setSubCommands(
            new ListLanguagesSub(bot, this),
            new SetLanguageSub(bot, this),
            new ListManagersSub(bot, this),
            new ManagerAddSub(bot, this),
            new ManagerRemoveSub(bot, this)
        );
        this.setupMessages(Text.ADMIN_EMBED_TITLE, Text.GENERIC_EMBED_FOOTER, Text.ADMIN_EMBED_CONTENT, language -> replacer -> replacer.set("flag", language.getFlag()));
    }

    @Override
    protected CommandData createCommandData() {
        return new CommandData("gadmin", "Manage SmartGiveaways server settings")
            .setDefaultEnabled(false)
            .addSubcommandGroups(
                new SubcommandGroupData("language", "Language server settings")
                    .addSubcommands(
                        new SubcommandData("list", "List the available bot languages"),
                        new SubcommandData("set", "Set the server's bot language")
                            .addOptions(
                                new OptionData(OptionType.STRING, "language", "Set the server's bot language", true)
                                    .addChoices(
                                        this.bot.getLanguageRegistry().languageMap()
                                            .values()
                                            .stream()
                                            .map(language -> new Command.Choice(language.getName(), language.getIdentifier()))
                                            .collect(Collectors.toSet())
                                    )
                            )
                    ),
                new SubcommandGroupData("manager", "Manager server settings")
                    .addSubcommands(
                        new SubcommandData("list", "List the server managers"),
                        new SubcommandData("add", "Add a server manager")
                            .addOption(OptionType.ROLE, "role", "Add a role as a server manager", true),
                        new SubcommandData("remove", "Remove a server manager")
                            .addOption(OptionType.ROLE, "role", "Remove a role as a server manager", true)
                    )

            );
    }
}
