package pink.zak.giveawaybot.discord.service.message;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.lang.LanguageRegistry;
import pink.zak.giveawaybot.discord.listener.reaction.pageable.Page;
import pink.zak.giveawaybot.discord.listener.reaction.pageable.PageableReactionListener;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.service.BotConstants;
import pink.zak.giveawaybot.discord.service.colour.Palette;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class PageableEmbedMenu extends PageableMenu implements PageableReactionListener {
    protected final LanguageRegistry languageRegistry;
    protected final Palette palette;
    private final GiveawayBot bot;

    private final Map<Integer, MessageEmbed> cachedPages = Maps.newConcurrentMap();
    private Message message;
    protected final Server server;
    private final boolean managerOnly;

    private ScheduledFuture<?> scheduledFuture;

    protected PageableEmbedMenu(GiveawayBot bot, Server server, boolean managerOnly) {
        this.languageRegistry = bot.getLanguageRegistry();
        this.palette = bot.getDefaults().getPalette();
        this.bot = bot;

        this.server = server;
        this.managerOnly = managerOnly;

        bot.registerListeners(this);
    }

    public void sendInitialMessage(TextChannel channel) {
        MessageEmbed embed = this.createPage(super.currentPage.get());
        this.cachedPages.put(super.currentPage.get(), embed);
        channel.sendMessage(embed).queue(sentMessage -> {
            if (super.maxPage > 1) {
                sentMessage.addReaction(BotConstants.BACK_ARROW).queue();
                sentMessage.addReaction(BotConstants.FORWARD_ARROW).queue();
            }
            this.message = sentMessage;
            this.scheduleDeletion();
        });
    }

    public abstract MessageEmbed createPage(int page);

    @Override
    public void drawPage(int page) {
        if (page > this.maxPage) {
            return;
        }
        MessageEmbed embed = this.cachedPages.computeIfAbsent(page, this::createPage);
        this.message.editMessage(embed).queue();
    }

    @Override
    public long getMessageId() {
        return this.message == null ? 0 : this.message.getIdLong();
    }

    @Override
    public void onReactionAdd(Page page, GuildMessageReactionAddEvent event) {
        if (this.managerOnly && !this.server.canMemberManage(event.getMember())) {
            return;
        }
        this.scheduleDeletion();
        if (page == Page.NEXT) {
            this.nextPage();
        } else {
            this.previousPage();
        }
    }

    public void scheduleDeletion() {
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(false);
        }
        this.scheduledFuture = this.bot.getThreadManager().getScheduler().schedule(() -> this.bot.unRegisterListeners(this), 1, TimeUnit.MINUTES);
    }
}
