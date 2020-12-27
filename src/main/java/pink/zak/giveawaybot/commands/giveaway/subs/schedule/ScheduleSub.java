package pink.zak.giveawaybot.commands.giveaway.subs.schedule;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.giveaway.GiveawayCmdUtils;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;
import pink.zak.giveawaybot.service.command.global.SubCommandUtils;
import pink.zak.giveawaybot.service.time.Time;
import pink.zak.giveawaybot.service.types.NumberUtils;

import java.util.List;

public class ScheduleSub extends SubCommand {
    private final GiveawayCmdUtils cmdUtils;

    public ScheduleSub(GiveawayBot bot, GiveawayCmdUtils cmdUtils) {
        super(bot, true, true, true);
        this.cmdUtils = cmdUtils;

        this.addFlat("schedule");
        this.addArgument(String.class); // Preset name
        this.addArgument(String.class); // Time to start in millis
        this.addArgument(String.class); // Length in millis
        this.addArgument(Integer.class, NumberUtils::isLikelyInteger); // Winner Amount
        this.addArgument(String.class); // Giveaway Item
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        String presetName = this.parseArgument(args, event.getGuild(), 1);
        long timeUntil = Time.parse(this.parseArgument(args, event.getGuild(), 2));
        long lengthMillis = Time.parse(this.parseArgument(args, event.getGuild(), 3));
        TextChannel responseChannel = event.getChannel();
        int winnerAmount = this.parseArgument(args, event.getGuild(), 4);
        String giveawayItem = String.join(" ", SubCommandUtils.getEnd(this.argsSize(), args));

        this.cmdUtils.schedule(server, presetName, timeUntil, lengthMillis, responseChannel, responseChannel, winnerAmount, giveawayItem);
    }
}
