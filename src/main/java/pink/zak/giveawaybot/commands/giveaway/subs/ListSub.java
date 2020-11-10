package pink.zak.giveawaybot.commands.giveaway.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.GiveawayCache;
import pink.zak.giveawaybot.controllers.GiveawayController;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public class ListSub extends SubCommand {
    private final GiveawayBot bot;
    private final Palette palette;
    private final GiveawayCache giveawayCache;
    private final GiveawayController giveawayController;

    public ListSub(GiveawayBot bot) {
        super(bot, true);
        this.addFlatWithAliases("list", "show");

        this.bot = bot;
        this.palette = bot.getDefaults().getPalette();
        this.giveawayCache = bot.getGiveawayCache();
        this.giveawayController = bot.getGiveawayController();
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        if (server.getActiveGiveaways().isEmpty()) {
            this.langFor(server, Text.NO_ACTIVE_GIVEAWAYS).to(event.getTextChannel());
            return;
        }
        StringBuilder descriptionBuilder = new StringBuilder();
            for (long giveawayId : server.getActiveGiveaways()) {
            CurrentGiveaway giveaway = this.giveawayCache.getSync(giveawayId);
            descriptionBuilder.append("[**")
                    .append(giveaway.giveawayItem())
                    .append("**](")
                    .append(this.giveawayController.getGiveawayMessage(giveaway).getJumpUrl())
                    .append(") -> (ID: ")
                    .append(giveaway.messageId())
                    .append(")");
        }
        event.getTextChannel().sendMessage(new EmbedBuilder()
                .setTitle(this.langFor(server, Text.GIVEAWAY_LIST_EMBED_TITLE).get())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).get())
                .setDescription(descriptionBuilder.toString())
                .setColor(this.palette.primary())
                .build()).queue();
    }
}
