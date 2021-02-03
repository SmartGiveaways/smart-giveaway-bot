package pink.zak.giveawaybot.discord.commands.menus;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.cache.UserCache;
import pink.zak.giveawaybot.discord.lang.Text;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.data.models.User;
import pink.zak.giveawaybot.discord.service.message.PageableEmbedMenu;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BanListMenu extends PageableEmbedMenu {
    private final List<User> bannedUsers = Lists.newArrayList();

    public BanListMenu(GiveawayBot bot, Server server) {
        super(bot, server, true);
        UserCache userCache = server.getUserCache();
        CompletableFuture.allOf(server.getBannedUsers().stream()
                .map(id -> userCache.getUserAsync(id).thenApply(this.bannedUsers::add))
                .collect(Collectors.toSet())
                .toArray(new CompletableFuture[]{})).join();
        super.maxPage = Math.max(1, (int) Math.ceil(this.bannedUsers.size() / 10.0));
    }

    @Override
    public MessageEmbed createPage(int page) {
        StringBuilder description = new StringBuilder();
        if (this.bannedUsers.isEmpty()) {
            description.append(this.languageRegistry.get(super.server, Text.BAN_LIST_NONE_BANNED));
        } else {
            for (int i = (page - 1) * 10; i < this.bannedUsers.size() && i < page * 10; i++) {
                User user = this.bannedUsers.get(i);
                description.append("<@")
                        .append(user.getId())
                        .append("> -> ")
                        .append(this.languageRegistry.get(super.server, user.isBanned() ? Text.BAN_LIST_BANNED : Text.BAN_LIST_SHADOW_BANNED))
                        .append("\n");
            }
        }
        return new EmbedBuilder()
                .setTitle(this.languageRegistry.get(super.server, Text.BAN_LIST_EMBED_TITLE).get())
                .setDescription(description.toString())
                .setFooter(this.languageRegistry.get(super.server, Text.BAN_LIST_FOOTER, replacer -> replacer
                        .set("page", page)
                        .set("max_page", super.maxPage)
                        .set("total", this.bannedUsers.size())).get())
                .setColor(super.palette.primary())
                .build();
    }
}
