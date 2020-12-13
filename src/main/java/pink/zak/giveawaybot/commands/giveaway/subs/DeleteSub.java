package pink.zak.giveawaybot.commands.giveaway.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public class DeleteSub extends SubCommand {
    private final GiveawayCache giveawayCache;
    private final GiveawayController giveawayController;

    public DeleteSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.addFlatWithAliases("delete", "remove");
        this.addArgument(Long.class);

        this.giveawayCache = bot.getGiveawayCache();
        this.giveawayController = bot.getGiveawayController();
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        long idInput = this.parseArgument(args, event.getGuild(), 1);
        TextChannel textChannel = event.getChannel();
        if (idInput < 779076362073145394L) { // Just check the ID isn't too old to reduce hits on the database.
            this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(textChannel);
            return;
        }
        this.giveawayCache.get(idInput).thenAccept(giveaway -> {
            if (giveaway == null) {
                this.langFor(server, Text.COULDNT_FIND_GIVEAWAY).to(textChannel);
                return;
            }
            this.giveawayController.deleteGiveaway(giveaway);
            this.langFor(server, Text.GIVEAWAY_DELETED, replacer -> replacer.set("giveaway-item", giveaway.giveawayItem())).to(event.getChannel());
        });
    }
}
