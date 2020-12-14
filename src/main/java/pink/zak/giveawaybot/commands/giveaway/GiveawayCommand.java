package pink.zak.giveawaybot.commands.giveaway;

import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.giveaway.subs.DeleteSub;
import pink.zak.giveawaybot.commands.giveaway.subs.ListScheduledSub;
import pink.zak.giveawaybot.commands.giveaway.subs.ListSub;
import pink.zak.giveawaybot.commands.giveaway.subs.RerollSub;
import pink.zak.giveawaybot.commands.giveaway.subs.create.CreateSub;
import pink.zak.giveawaybot.commands.giveaway.subs.create.CreateWithChannelSub;
import pink.zak.giveawaybot.commands.giveaway.subs.schedule.ScheduleSub;
import pink.zak.giveawaybot.commands.giveaway.subs.schedule.ScheduleWithChannelSub;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.service.command.command.SimpleHelpCommand;

public class GiveawayCommand extends SimpleHelpCommand {

    public GiveawayCommand(GiveawayBot bot) {
        super(bot, "g", true, false);
        GiveawayCmdUtils cmdUtils = new GiveawayCmdUtils(bot);
        this.setAliases("giveaway");

        this.setSubCommands(
                new CreateSub(bot, cmdUtils),
                new CreateWithChannelSub(bot, cmdUtils),
                new ScheduleSub(bot, cmdUtils),
                new ScheduleWithChannelSub(bot, cmdUtils),
                new DeleteSub(bot, this),
                new ListScheduledSub(bot),
                new ListSub(bot),
                new RerollSub(bot)
        );

        this.buildMessages(Text.GIVEAWAY_HELP_EMBED_TITLE, Text.GIVEAWAY_HELP_EMBED_FOOTER, Text.GIVEAWAY_HELP_EMBED_CONTENT);
    }
}
