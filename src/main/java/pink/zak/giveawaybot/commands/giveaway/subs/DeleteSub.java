package pink.zak.giveawaybot.commands.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.commands.giveaway.GiveawayCommand;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.controllers.ScheduledGiveawayController;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.command.SubCommand;
import pink.zak.giveawaybot.service.types.NumberUtils;

import java.util.List;
import java.util.UUID;

public class DeleteSub extends SubCommand {
    private final GiveawayCommand giveawayCommand;
    private final GiveawayCache giveawayCache;
    private final GiveawayController giveawayController;
    private final ScheduledGiveawayCache scheduledGiveawayCache;
    private final ScheduledGiveawayController scheduledGiveawayController;

    public DeleteSub(GiveawayBot bot, GiveawayCommand giveawayCommand) {
        super(bot, true, false, false);
        this.addFlatWithAliases("delete", "remove");
        this.addArgument(String.class);

        this.giveawayCommand = giveawayCommand;
        this.giveawayCache = bot.getGiveawayCache();
        this.giveawayController = bot.getGiveawayController();
        this.scheduledGiveawayCache = bot.getScheduledGiveawayCache();
        this.scheduledGiveawayController = bot.getScheduledGiveawayController();
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        String input = this.parseArgument(args, event.getGuild(), 1);
        if (input.contains("-")) {
            if (this.deleteUuid(server, input, event.getChannel())) {
                this.giveawayCommand.onExecute(sender, server, event, args);
            }
        } else {
            if (this.deleteLong(server, input, event.getChannel())) {
                this.giveawayCommand.onExecute(sender, server, event, args);
            }
        }
    }

    private boolean deleteUuid(Server server, String input, TextChannel channel) {
        try {
            UUID uuid = UUID.fromString(input);
            this.scheduledGiveawayCache.get(uuid).thenAccept(giveaway -> {
                if (giveaway == null) {
                    this.langFor(server, Text.COULDNT_FIND_SCHEDULED_GIVEAWAY).to(channel);
                    return;
                }
                this.scheduledGiveawayController.deleteGiveaway(server, giveaway);
                this.langFor(server, Text.SCHEDULED_GIVEAWAY_DELETED, replacer -> replacer.set("item", giveaway.giveawayItem())).to(channel);
            });
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
            this.giveawayCache.get(id).thenAccept(giveaway -> {
                if (giveaway == null) {
                    this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(channel);
                    return;
                }
                this.giveawayController.deleteGiveaway(giveaway);
                this.langFor(server, Text.GIVEAWAY_DELETED, replacer -> replacer.set("item", giveaway.giveawayItem())).to(channel);
            });
        } catch (IllegalArgumentException ex) {
            return true;
        }
        return false;
    }
}
