package pink.zak.giveawaybot.commands.ban;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.ban.subs.BanListSub;
import pink.zak.giveawaybot.commands.ban.subs.BanSub;
import pink.zak.giveawaybot.commands.ban.subs.ShadowBanSub;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.service.command.discord.command.SimpleHelpCommand;

public class BanCommand extends SimpleHelpCommand {

    public BanCommand(GiveawayBot bot) {
        super(bot, "gban", true, false);

        BanCmdUtils cmdUtils = new BanCmdUtils(bot);
        BanListSub banListSub = new BanListSub(bot);
        this.setSubCommands(
                banListSub,
                new BanSub(bot, cmdUtils),
                new ShadowBanSub(bot, cmdUtils)
        );
        bot.registerListeners(banListSub);

        this.setupMessages(Text.BAN_EMBED_TITLE, Text.BAN_EMBED_CONTENT);
    }
}
