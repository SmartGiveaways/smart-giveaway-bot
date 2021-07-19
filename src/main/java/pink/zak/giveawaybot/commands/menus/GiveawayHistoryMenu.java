package pink.zak.giveawaybot.commands.menus;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.data.models.giveaway.finished.PartialFinishedGiveaway;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.message.PageableButtonEmbedMenu;
import pink.zak.giveawaybot.service.time.Time;

import java.util.List;
import java.util.StringJoiner;

public class GiveawayHistoryMenu extends PageableButtonEmbedMenu {
    private final List<PartialFinishedGiveaway> finishedGiveaways;

    public GiveawayHistoryMenu(GiveawayBot bot, Server server) {
        super(bot, server);
        this.finishedGiveaways = bot.getFinishedGiveawayCache().getAllPartial(server);
        super.maxPage = (int) Math.ceil(this.finishedGiveaways.size() / 10.0);
    }

    @Override
    public MessageEmbed createPage(int page) {
        StringJoiner description = new StringJoiner("\n");
        if (this.finishedGiveaways.isEmpty()) {
            description.add(this.languageRegistry.get(super.server, Text.GIVEAWAY_HISTORY_NO_HISTORY).toString());
        } else {
            for (int i = (page - 1) * 10; i < this.finishedGiveaways.size() && i < page * 10; i++) {
                PartialFinishedGiveaway giveaway = this.finishedGiveaways.get(i);
                description.add(
                        super.languageRegistry.get(super.server, Text.GIVEAWAY_HISTORY_EMBED_LINE, replacer -> replacer
                                .set("item", giveaway.getLinkedGiveawayItem())
                                .set("time", Time.formatAsDateTime(giveaway.getEndTime()) + " UTC")
                                .set("id", giveaway.getMessageId())).toString()
                );
            }
        }
        return new EmbedBuilder()
                .setTitle(this.languageRegistry.get(super.server, Text.GIVEAWAY_HISTORY_EMBED_TITLE).toString())
                .setDescription(description.toString())
                .setFooter(this.languageRegistry.get(super.server, Text.GIVEAWAY_HISTORY_EMBED_FOOTER, replacer -> replacer
                        .set("page", page)
                        .set("max_page", super.maxPage)
                        .set("total", this.finishedGiveaways.size())).toString())
                .setColor(super.palette.primary())
                .build();
    }
}
