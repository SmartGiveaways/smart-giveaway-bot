package pink.zak.giveawaybot.commands.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.giveaway.GiveawayCmdUtils;
import pink.zak.giveawaybot.service.command.command.SubCommand;
import pink.zak.giveawaybot.service.time.Time;
import pink.zak.giveawaybot.service.types.StringUtils;

import java.util.List;

public class CreateSub extends SubCommand {

    private final GiveawayCmdUtils cmdUtils;

    public CreateSub(GiveawayBot bot, GiveawayCmdUtils cmdUtils) {
        super(bot, false, true);
        this.cmdUtils = cmdUtils;

        this.addFlat("create");
        this.addArgument(String.class);
        this.addArgument(String.class);
        this.addArgument(Integer.class, StringUtils::isNumerical);
        this.addArgument(String.class);
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        String presetName = this.parseArgument(args, event.getGuild(), 1);
        long lengthMillis = Time.parse(this.parseArgument(args, event.getGuild(), 2));
        TextChannel responseChannel = event.getTextChannel();
        int winnerAmount = this.parseArgument(args, event.getGuild(), 3);
        String giveawayItem = String.join(" ", this.getEnd(args));

        this.cmdUtils.create(lengthMillis, winnerAmount, presetName, giveawayItem, responseChannel, responseChannel);
    }
}
