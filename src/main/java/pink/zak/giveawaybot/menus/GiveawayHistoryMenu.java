package pink.zak.giveawaybot.menus;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.service.message.PageableEmbedMenu;
import pink.zak.giveawaybot.service.time.Time;

import java.util.List;

public class GiveawayHistoryMenu extends PageableEmbedMenu {
    private final List<FinishedGiveaway> finishedGiveaways;

    public GiveawayHistoryMenu(GiveawayBot bot, Server server) {
        super(bot);
        super.cooldown = 500;
        this.finishedGiveaways = bot.getFinishedGiveawayCache().getAll(server, true);
        super.maxPage = (int) Math.ceil(this.finishedGiveaways.size() / 10.0);
    }

    @Override
    public MessageEmbed createPage(int page) {
        System.out.println("Asked to create page " + page);
        StringBuilder description = new StringBuilder();
        for (int i = (page - 1) * 10; i < this.finishedGiveaways.size() && i < page * 10; i++) {
            FinishedGiveaway giveaway = this.finishedGiveaways.get(i);
            description.append(giveaway.linkedGiveawayItem())
                    .append(" - ended ")
                    .append(Time.formatAsDateTime(giveaway.endTime()))
                    .append(" UTC\n");
        }
        return new EmbedBuilder()
                .setTitle("**Historical Giveaways**")
                .setDescription(description.toString())
                .setFooter(page + "/" + super.maxPage + " | " + this.finishedGiveaways.size() + " total finished giveaways")
                .setColor(super.palette.primary())
                .build();
    }
}
