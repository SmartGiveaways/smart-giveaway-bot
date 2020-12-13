package pink.zak.giveawaybot.commands.ban.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.cache.CacheBuilder;
import pink.zak.giveawaybot.service.cache.caches.Cache;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SubCommand;
import pink.zak.giveawaybot.service.tuple.MutablePair;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BanListSub extends SubCommand implements EventListener {
    private final Palette palette;
    private final ServerCache serverCache;
    private final Cache<Long, MutablePair<Message, Integer>> activeLists;

    public BanListSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.addFlat("list");

        this.palette = bot.getDefaults().getPalette();
        this.serverCache = bot.getServerCache();
        this.activeLists = new CacheBuilder<Long, MutablePair<Message, Integer>>()
                .setControlling(bot)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .setRemovalAction(value -> {
                    try {
                        value.getKey().clearReactions().queue();
                    } catch (ErrorResponseException ignored) {}
                })
                .build();
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        int pages = (int) Math.ceil(server.getBannedUsers().size() / 10.0);
        event.getChannel().sendMessage(this.buildEmbed(server, pages, 1)).queue(embed -> {
            if (pages > 1) {
                this.activeLists.set(embed.getIdLong(), MutablePair.of(embed, 1));
                embed.addReaction("\u2B05").queue();
                embed.addReaction("\u27A1").queue();
            }
        });
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (!(genericEvent instanceof GuildMessageReactionAddEvent event)) {
            return;
        }
        long messageId = event.getMessageIdLong();
        if (event.getUser().isBot() || event.getReactionEmote().isEmote() || !this.activeLists.contains(messageId)) {
            return;
        }
        this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
            String emoji = event.getReactionEmote().getEmoji();
            if (!emoji.equals("\u2B05") && !emoji.equals("\u27A1") || !server.canMemberManage(event.getMember())) {
                return;
            }
            int totalPages = (int) Math.ceil(server.getBannedUsers().size() / 10.0);
            MutablePair<Message, Integer> messageAndPage = this.activeLists.getSync(messageId);
            if (messageAndPage == null) {
                return;
            }
            if (emoji.equals("\u2B05")) { // Go back a page
                int newPage = messageAndPage.getValue() - 1;
                if (newPage > 0) {
                    messageAndPage.getKey().editMessage(this.buildEmbed(server, totalPages, messageAndPage.getValue() - 1)).queue();
                    messageAndPage.setValue(newPage);
                }
                return;
            } // Rest must be go forward a page
            int newPage = messageAndPage.getValue() + 1;
            if (newPage <= totalPages) {
                messageAndPage.getKey().editMessage(this.buildEmbed(server, totalPages, messageAndPage.getValue() + 1)).queue();
                messageAndPage.setValue(newPage);
            }
        }).exceptionally(ex -> {
            GiveawayBot.getLogger().error("BanListSub Page Reaction Listener: ", ex);
            return null;
        });

    }

    private MessageEmbed buildEmbed(Server server, int totalPages, int page) {
        StringBuilder descriptionBuilder = new StringBuilder();
        for (int i = (page - 1) * 10; i < server.getBannedUsers().size() && i < page * 10; i++) {
            long id = server.getBannedUsers().get(i);
            descriptionBuilder.append("<@")
                    .append(id)
                    .append("> -> ")
                    .append(this.langFor(server, server.getUserCache().getSync(id).isBanned() ? Text.BAN_LIST_BANNED : Text.BAN_LIST_SHADOW_BANNED))
                    .append("\n");
        }
        return new EmbedBuilder()
                .setTitle(this.langFor(server, Text.BAN_LIST_EMBED_TITLE).get())
                .setFooter(this.langFor(server, totalPages > 1 ? Text.BAN_LIST_PAGE_FOOTER : Text.BAN_LIST_FOOTER, replacer -> replacer
                        .set("amount", server.getBannedUsers().size())
                        .set("page", page)
                        .set("maxPage", totalPages)).get())
                .setDescription(descriptionBuilder.toString())
                .setColor(this.palette.primary())
                .build();
    }
}
