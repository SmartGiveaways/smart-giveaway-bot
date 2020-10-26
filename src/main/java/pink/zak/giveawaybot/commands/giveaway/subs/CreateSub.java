package pink.zak.giveawaybot.commands.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.controller.GiveawayController;
import pink.zak.giveawaybot.service.command.command.SubCommand;
import pink.zak.giveawaybot.service.time.Time;

import java.util.List;

public class CreateSub extends SubCommand {
    private final GiveawayController giveawayController;

    public CreateSub(GiveawayBot bot) {
        super(bot, false, true);
        this.giveawayController = bot.getGiveawayController();

        this.addFlat("create");
        this.addArgument(String.class);
        this.addArgument(String.class);
        this.addArgument(TextChannel.class);
        this.addArgument(Integer.class);
        this.addArgument(String.class);
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        String presetName = this.parseArgument(args, event.getGuild(), 1);
        long lengthMillis = Time.parse(this.parseArgument(args, event.getGuild(), 2));
        TextChannel responseChannel = event.getTextChannel();
        TextChannel giveawayChannel = this.parseArgument(args, event.getGuild(), 3);
        int winnerAmount = this.parseArgument(args, event.getGuild(), 4);
        String giveawayItem = String.join(" ", this.getEnd(args));
        if (this.performChecks(lengthMillis, giveawayChannel, giveawayItem, responseChannel)) {
            return;
        }
        switch (this.giveawayController.createGiveaway(lengthMillis, winnerAmount, giveawayChannel, presetName, giveawayItem).getRight()) {
            case GIVEAWAY_LIMIT_FAILURE:
                event.getChannel().sendMessage(":x: Your guild has reached the maximum number of 5 giveaways. Gimme all ur money for more or delete some by deleting their message.").queue();
                break;
            case WINNER_LIMIT_FAILURE:
                event.getChannel().sendMessage(":x: You can only have a maximum of 5 winners per giveaway. Gimme all ur money if u want more.").queue();
                break;
            case NO_PRESET:
                event.getChannel().sendMessage(":x: Could not find the specified preset. Use `>preset list` to list them all.").queue();
                break;
            case GENERIC_FAILURE:
            case RATE_LIMIT_FAILURE:
                event.getChannel().sendMessage(":x: An error occurred. Try again in a minute when the bot is less busy.").queue();
                break;
            case SUCCESS:
                if (responseChannel != giveawayChannel) {
                    event.getChannel().sendMessage(":white_check_mark: Created your giveaway in " + giveawayChannel.getAsMention() + ".").queue();
                }
        }
    }

    private boolean performChecks(long length, TextChannel giveawayChannel, String giveawayItem, TextChannel responseChannel) {
        if (length < 30000) {
            responseChannel.sendMessage("Giveaways must be at least 30 seconds long.").queue();
            return true;
        }
        if (length > 5184000000L) {
            responseChannel.sendMessage("Giveaways must be no longer than 30 days.").queue();
            return true;
        }
        if (giveawayChannel == null) {
            responseChannel.sendMessage("Could not find the specified channel.").queue();
            return true;
        }
        if (giveawayItem.isEmpty() || giveawayItem.equals(" ") || giveawayItem.length() > 20) {
            responseChannel.sendMessage("Issue parsing giveaway reward.").queue();
            return true;
        }
        return false;
    }
}
