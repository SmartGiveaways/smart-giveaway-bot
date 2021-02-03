package pink.zak.giveawaybot.discord.commands.menus;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.data.models.giveaway.finished.PartialFinishedGiveaway;
import pink.zak.giveawaybot.discord.lang.Text;
import pink.zak.giveawaybot.discord.service.message.PageableEmbedMenu;
import pink.zak.giveawaybot.discord.service.time.Time;

import java.util.List;

public class GiveawayHistoryMenu extends PageableEmbedMenu {
    private final List<PartialFinishedGiveaway> finishedGiveaways;

    public GiveawayHistoryMenu(GiveawayBot bot, Server server) {
        super(bot, server, true);
        super.cooldown = 500;
        this.finishedGiveaways = bot.getFinishedGiveawayCache().getAllPartial(server);
        super.maxPage = (int) Math.ceil(this.finishedGiveaways.size() / 10.0);
    }

    @Override
    public MessageEmbed createPage(int page) {
        StringBuilder description = new StringBuilder();
        if (this.finishedGiveaways.isEmpty()) {
            description.append(this.languageRegistry.get(super.server, Text.GIVEAWAY_HISTORY_NO_HISTORY));
        } else {
            for (int i = (page - 1) * 10; i < this.finishedGiveaways.size() && i < page * 10; i++) {
                PartialFinishedGiveaway giveaway = this.finishedGiveaways.get(i);
                description.append(
                        super.languageRegistry.get(super.server, Text.GIVEAWAY_HISTORY_EMBED_LINE, replacer -> replacer
                                .set("item", giveaway.getLinkedGiveawayItem())
                                .set("time", Time.formatAsDateTime(giveaway.getEndTime()) + " UTC")
                                .set("id", giveaway.getMessageId())).toString()
                );
            }
        }
        return new EmbedBuilder()
                .setTitle(this.languageRegistry.get(super.server, Text.GIVEAWAY_HISTORY_EMBED_TITLE).get())
                .setDescription(description.toString())
                .setFooter(this.languageRegistry.get(super.server, Text.GIVEAWAY_HISTORY_EMBED_FOOTER, replacer -> replacer
                        .set("page", page)
                        .set("max_page", super.maxPage)
                        .set("total", this.finishedGiveaways.size())).get())
                .setColor(super.palette.primary())
                .build();
    }
}
