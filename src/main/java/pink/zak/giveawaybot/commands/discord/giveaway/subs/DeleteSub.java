package pink.zak.giveawaybot.commands.discord.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.discord.giveaway.GiveawayCommand;
import pink.zak.giveawaybot.controllers.ScheduledGiveawayController;
import pink.zak.giveawaybot.data.cache.GiveawayCache;
import pink.zak.giveawaybot.data.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.data.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.pipelines.giveaway.steps.DeletionStep;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;
import pink.zak.giveawaybot.service.types.NumberUtils;

import java.util.List;
import java.util.UUID;

public class DeleteSub extends SubCommand {
    private final GiveawayCommand giveawayCommand;
    private final GiveawayCache giveawayCache;
    private final ScheduledGiveawayCache scheduledGiveawayCache;
    private final ScheduledGiveawayController scheduledGiveawayController;
    private final DeletionStep deletionStep;

    public DeleteSub(GiveawayBot bot, GiveawayCommand giveawayCommand) {
        super(bot, true, false, false);
        this.addFlatWithAliases("delete", "remove");
        this.addArgument(String.class);

        this.giveawayCommand = giveawayCommand;
        this.giveawayCache = bot.getGiveawayCache();
        this.scheduledGiveawayCache = bot.getScheduledGiveawayCache();
        this.scheduledGiveawayController = bot.getScheduledGiveawayController();
        this.deletionStep = new DeletionStep(bot);
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        String input = this.parseArgument(args, event.getGuild(), 1);
        if (input.contains("-")) {
            if (this.deleteUuid(server, input, event.getChannel())) {
                this.giveawayCommand.onExecute(sender, server, event, args);
            }
        } else if (this.deleteLong(server, input, event.getChannel())) {
            this.giveawayCommand.onExecute(sender, server, event, args);
        }
    }

    private boolean deleteUuid(Server server, String input, TextChannel channel) {
        try {
            UUID uuid = UUID.fromString(input);
            ScheduledGiveaway giveaway = this.scheduledGiveawayCache.get(uuid);
            if (giveaway == null) {
                this.langFor(server, Text.COULDNT_FIND_SCHEDULED_GIVEAWAY).to(channel);
                return false;
            }
            this.scheduledGiveawayController.deleteGiveaway(server, giveaway);
            this.langFor(server, Text.SCHEDULED_GIVEAWAY_DELETED, replacer -> replacer.set("item", giveaway.getGiveawayItem())).to(channel);
        } catch (IllegalArgumentException ex) {
            return true;
        }
        return false;
    }

    private boolean deleteLong(Server server, String input, TextChannel channel) {
        if (!NumberUtils.isNumerical(input)) {
            return true;
        }
        try {
            long id = Long.parseLong(input);
            if (id < 786066350882488381L) { // Just check the ID isn't too old to reduce hits on the database.
                this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(channel);
                return false;
            }
            CurrentGiveaway giveaway = this.giveawayCache.get(id);
            if (giveaway == null) {
                this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(channel);
                return false;
            }
            this.deletionStep.delete(giveaway);
            this.langFor(server, Text.GIVEAWAY_DELETED, replacer -> replacer.set("item", giveaway.getGiveawayItem())).to(channel);
        } catch (IllegalArgumentException ex) {
            return true;
        }
        return false;
    }
}
