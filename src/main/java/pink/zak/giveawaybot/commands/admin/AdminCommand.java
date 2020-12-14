package pink.zak.giveawaybot.commands.admin;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.admin.subs.language.ListLanguagesSub;
import pink.zak.giveawaybot.commands.admin.subs.language.SetLanguageSub;
import pink.zak.giveawaybot.commands.admin.subs.manager.ListManagersSub;
import pink.zak.giveawaybot.commands.admin.subs.manager.ManagerAddSub;
import pink.zak.giveawaybot.commands.admin.subs.manager.ManagerRemoveSub;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.service.command.command.SimpleHelpCommand;

public class AdminCommand extends SimpleHelpCommand {

    public AdminCommand(GiveawayBot bot) {
        super(bot, "gadmin", true, false);

        this.setAliases("gmanage", "gmng", "gadmn");
        this.setSubCommands(
                new ListLanguagesSub(bot),
                new SetLanguageSub(bot),
                new ListManagersSub(bot),
                new ManagerAddSub(bot),
                new ManagerRemoveSub(bot)
        );
        this.buildMessages(Text.ADMIN_EMBED_TITLE, Text.ADMIN_EMBED_CONTENT);
    }
}
