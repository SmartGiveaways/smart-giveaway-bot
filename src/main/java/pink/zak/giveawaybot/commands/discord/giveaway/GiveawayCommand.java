package pink.zak.giveawaybot.commands.discord.giveaway;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.DeleteSub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.HistorySub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.ListScheduledSub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.ListSub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.RerollSub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.create.CreateSub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.create.CreateWithChannelSub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.info.InfoSubLong;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.info.InfoSubUuid;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.schedule.ScheduleSub;
import pink.zak.giveawaybot.commands.discord.giveaway.subs.schedule.ScheduleWithChannelSub;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.command.discord.command.SimpleHelpCommand;

public class GiveawayCommand extends SimpleHelpCommand {

    public GiveawayCommand(GiveawayBot bot) {
        super(bot, "g", true, false);
        GiveawayCmdUtils cmdUtils = new GiveawayCmdUtils(bot);
        this.setAliases("giveaway");

        this.setSubCommands(
                new CreateSub(bot, cmdUtils),
                new CreateWithChannelSub(bot, cmdUtils),
                new InfoSubLong(bot),
                new InfoSubUuid(bot),
                new ScheduleSub(bot, cmdUtils),
                new ScheduleWithChannelSub(bot, cmdUtils),
                new DeleteSub(bot, this),
                new HistorySub(bot),
                new ListScheduledSub(bot),
                new ListSub(bot),
                new RerollSub(bot)
        );

        this.setupMessages(Text.GIVEAWAY_HELP_EMBED_TITLE, Text.GIVEAWAY_HELP_EMBED_FOOTER, Text.GIVEAWAY_HELP_EMBED_CONTENT);
    }
}
