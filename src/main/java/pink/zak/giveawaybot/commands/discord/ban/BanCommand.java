package pink.zak.giveawaybot.commands.discord.ban;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.discord.ban.subs.BanListSub;
import pink.zak.giveawaybot.commands.discord.ban.subs.BanSub;
import pink.zak.giveawaybot.commands.discord.ban.subs.ShadowBanSub;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.command.discord.command.SimpleHelpCommand;

public class BanCommand extends SimpleHelpCommand {

    public BanCommand(GiveawayBot bot) {
        super(bot, "gban", true, false);

        BanCmdUtils cmdUtils = new BanCmdUtils(bot);
        this.setSubCommands(
                new BanListSub(bot),
                new BanSub(bot, cmdUtils),
                new ShadowBanSub(bot, cmdUtils)
        );
        this.setupMessages(Text.BAN_EMBED_TITLE, Text.BAN_EMBED_CONTENT);
    }
}
