package pink.zak.giveawaybot.commands.ban.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.cache.Cache;
import pink.zak.giveawaybot.service.cache.CacheBuilder;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BanListSub extends SubCommand {
    private final Palette palette;
    private final Cache<Long, Message> activeLists;

    public BanListSub(GiveawayBot bot) {
        super(bot, true);
        this.addFlat("list");

        this.palette = bot.getDefaults().getPalette();
        this.activeLists = new CacheBuilder<Long, Message>().setControlling(bot).expireAfterAccess(10, TimeUnit.MINUTES).build();
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        int pages = (int) Math.ceil(server.getBannedUsers().size() / 10.0);
        StringBuilder descriptionBuilder = new StringBuilder();
        for (int i = 0; i < server.getBannedUsers().size() && i < 10; i++) {
            long id = server.getBannedUsers().get(i);
            descriptionBuilder.append("<@")
                    .append(id)
                    .append("> -> ")
                    .append(server.getUserCache().getSync(id).isBanned() ? "banned" : "shadow banned")
                    .append("\n");
        }

        event.getTextChannel().sendMessage(new EmbedBuilder()
                .setTitle(this.langFor(server, Text.BAN_LIST_EMBED_TITLE).get())
                .setFooter(this.langFor(server, pages > 1 ? Text.BAN_LIST_PAGE_FOOTER : Text.BAN_LIST_FOOTER, replacer -> replacer
                        .set("amount", server.getBannedUsers().size())
                        .set("page", 1)
                        .set("maxPage", pages)).get())
                .setDescription(descriptionBuilder.toString())
                .setColor(this.palette.primary())
                .build()).queue(embed -> {
            if (pages > 1) {
                this.activeLists.set(embed.getIdLong(), embed);
                embed.addReaction("\u2B05").queue();
                embed.addReaction("\u27A1").queue();
            }
        });
    }
}
