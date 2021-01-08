package pink.zak.giveawaybot.discord.menus;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.message.PageableEmbedMenu;
import pink.zak.giveawaybot.discord.service.time.Time;
import pink.zak.giveawaybot.discord.lang.enums.Text;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.models.giveaway.FinishedGiveaway;

import java.util.List;

public class GiveawayHistoryMenu extends PageableEmbedMenu {
    private final List<FinishedGiveaway> finishedGiveaways;
    private final Server server;

    public GiveawayHistoryMenu(GiveawayBot bot, Server server) {
        super(bot, server, true);
        super.cooldown = 500;
        this.finishedGiveaways = bot.getFinishedGiveawayCache().getAll(server);
        super.maxPage = (int) Math.ceil(this.finishedGiveaways.size() / 10.0);
        this.server = server;
    }

    @Override
    public MessageEmbed createPage(int page) {
        for (FinishedGiveaway giveaway : this.finishedGiveaways) {
            giveaway.setEndTime(giveaway.getEndTime() + 1);
        }
        StringBuilder description = new StringBuilder();
        for (int i = (page - 1) * 10; i < this.finishedGiveaways.size() && i < page * 10; i++) {
            FinishedGiveaway giveaway = this.finishedGiveaways.get(i);
            description.append(
                    super.languageRegistry.get(this.server, Text.GIVEAWAY_HISTORY_EMBED_LINE, replacer -> replacer
                            .set("item", giveaway.getLinkedGiveawayItem())
                            .set("time", Time.formatAsDateTime(giveaway.getEndTime()) + " UTC")
                            .set("id", giveaway.getMessageId())).toString()
            );
        }
        return new EmbedBuilder()
                .setTitle(this.languageRegistry.get(this.server, Text.GIVEAWAY_HISTORY_EMBED_TITLE).get())
                .setDescription(description.toString())
                .setFooter(this.languageRegistry.get(this.server, Text.GIVEAWAY_HISTORY_EMBED_FOOTER, replacer -> replacer
                        .set("page", page)
                        .set("max_page", super.maxPage)
                        .set("total", this.finishedGiveaways.size())).get())
                .setColor(super.palette.primary())
                .build();
    }
}
