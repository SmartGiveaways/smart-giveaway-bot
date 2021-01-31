package pink.zak.giveawaybot.discord.commands.discord.giveaway.subs.create;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.commands.discord.giveaway.GiveawayCmdUtils;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;
import pink.zak.giveawaybot.discord.service.command.global.SubCommandUtils;
import pink.zak.giveawaybot.discord.service.time.Time;
import pink.zak.giveawaybot.discord.service.types.NumberUtils;

import java.util.List;

public class CreateWithChannelSub extends SubCommand {
    private final GiveawayCmdUtils cmdUtils;

    public CreateWithChannelSub(GiveawayBot bot, GiveawayCmdUtils cmdUtils) {
        super(bot, true, false, true);
        this.cmdUtils = cmdUtils;

        this.addFlat("create");
        this.addArgument(String.class); // preset name
        this.addArgument(String.class); // length
        this.addArgument(TextChannel.class); // giveaway channel
        this.addArgument(Integer.class, NumberUtils::isLikelyInteger); // winner amount
        this.addArgument(String.class); // giveaway item placer
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        String presetName = this.parseArgument(args, event.getGuild(), 1);
        long lengthMillis = Time.parse(this.parseArgument(args, event.getGuild(), 2));
        TextChannel responseChannel = event.getChannel();
        TextChannel giveawayChannel = this.parseArgument(args, event.getGuild(), 3);
        int winnerAmount = this.parseArgument(args, event.getGuild(), 4);
        String giveawayItem = String.join(" ", SubCommandUtils.getEnd(this.argsSize(), args));

        this.cmdUtils.create(server, lengthMillis, winnerAmount, presetName, giveawayItem, giveawayChannel, responseChannel);
    }
}
