package pink.zak.giveawaybot.commands.discord.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.controllers.ScheduledGiveawayController;
import pink.zak.giveawaybot.data.cache.GiveawayCache;
import pink.zak.giveawaybot.data.cache.ScheduledGiveawayCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.data.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.data.models.giveaway.ScheduledGiveaway;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.pipelines.giveaway.steps.DeletionStep;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

import java.util.UUID;

public class DeleteSub extends SubCommand {
    private final GiveawayCache giveawayCache;
    private final ScheduledGiveawayCache scheduledGiveawayCache;
    private final ScheduledGiveawayController scheduledGiveawayController;
    private final DeletionStep deletionStep;

    public DeleteSub(GiveawayBot bot) {
        super(bot, "delete", true, false);

        this.giveawayCache = bot.getGiveawayCache();
        this.scheduledGiveawayCache = bot.getScheduledGiveawayCache();
        this.scheduledGiveawayController = bot.getScheduledGiveawayController();
        this.deletionStep = new DeletionStep(bot);
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        String input = event.getOption("giveawayid").getAsString();
        if (input.contains("-"))
            this.deleteUuid(server, input, event);
        else
            this.deleteLong(server, input, event);
    }

    private void deleteUuid(Server server, String input, SlashCommandEvent event) {
        UUID uuid;
        try {
            uuid = UUID.fromString(input);
        } catch (IllegalArgumentException ex) {
            this.langFor(server, Text.COULDNT_FIND_SCHEDULED_GIVEAWAY).to(event);
            return;
        }
        ScheduledGiveaway giveaway = this.scheduledGiveawayCache.get(uuid);
        if (giveaway == null) {
            this.langFor(server, Text.COULDNT_FIND_SCHEDULED_GIVEAWAY).to(event);
            return;
        }
        this.scheduledGiveawayController.deleteGiveaway(server, giveaway);
        this.langFor(server, Text.SCHEDULED_GIVEAWAY_DELETED, replacer -> replacer.set("item", giveaway.getGiveawayItem())).to(event);
    }

    private void deleteLong(Server server, String input, SlashCommandEvent event) {
        long id;
        try {
            id = Long.parseLong(input);
        } catch (IllegalArgumentException ex) {
            this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(event);
            return;
        }
        if (id < 786066350882488381L) { // Just check the ID isn't too old to reduce hits on the database.
            this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(event);
            return;
        }
        CurrentGiveaway giveaway = this.giveawayCache.get(id);
        if (giveaway == null) {
            this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(event);
            return;
        }
        this.deletionStep.delete(giveaway);
        this.langFor(server, Text.GIVEAWAY_DELETED, replacer -> replacer.set("item", giveaway.getGiveawayItem())).to(event);
    }
}
