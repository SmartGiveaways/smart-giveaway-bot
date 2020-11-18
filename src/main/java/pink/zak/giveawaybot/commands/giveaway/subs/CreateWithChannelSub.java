package pink.zak.giveawaybot.commands.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.giveaway.GiveawayCmdUtils;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.command.SubCommand;
import pink.zak.giveawaybot.service.time.Time;
import pink.zak.giveawaybot.service.types.NumberUtils;

import java.util.List;

public class CreateWithChannelSub extends SubCommand {
    private final GiveawayCmdUtils cmdUtils;

    public CreateWithChannelSub(GiveawayBot bot, GiveawayCmdUtils cmdUtils) {
        super(bot, false, true);
        this.cmdUtils = cmdUtils;

        this.addFlat("create");
        this.addArgument(String.class); // preset name
        this.addArgument(String.class); // length
        this.addArgument(TextChannel.class); // giveaway channel
        this.addArgument(Integer.class, NumberUtils::isInteger); // winner amount
        this.addArgument(String.class); // giveaway item placer
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        String presetName = this.parseArgument(args, event.getGuild(), 1);
        long lengthMillis = Time.parse(this.parseArgument(args, event.getGuild(), 2));
        TextChannel responseChannel = event.getTextChannel();
        TextChannel giveawayChannel = this.parseArgument(args, event.getGuild(), 3);
        int winnerAmount = this.parseArgument(args, event.getGuild(), 4);
        String giveawayItem = String.join(" ", this.getEnd(args));

        this.cmdUtils.create(server, lengthMillis, winnerAmount, presetName, giveawayItem, giveawayChannel, responseChannel);
    }
}
