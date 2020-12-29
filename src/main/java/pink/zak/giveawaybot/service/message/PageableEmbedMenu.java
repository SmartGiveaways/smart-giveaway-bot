package pink.zak.giveawaybot.service.message;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.listener.reaction.pageable.Page;
import pink.zak.giveawaybot.listener.reaction.pageable.PageableReactionListener;
import pink.zak.giveawaybot.service.colour.Palette;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class PageableEmbedMenu extends PageableMenu implements PageableReactionListener {
    protected final Palette palette;

    private final Map<Integer, MessageEmbed> cachedPages = Maps.newHashMap();
    private Message message;

    protected PageableEmbedMenu(GiveawayBot bot) {
        this.palette = bot.getDefaults().getPalette();
        bot.getThreadManager().getScheduler().schedule(() -> this.delete(bot), 60, TimeUnit.SECONDS);

        bot.registerListeners(this);
    }

    public void sendInitialMessage(TextChannel channel) {
        MessageEmbed embed = this.createPage(super.currentPage.get());
        this.cachedPages.put(super.currentPage.get(), embed);
        channel.sendMessage(embed).queue(message -> {
            this.message = message;
            message.addReaction("\u2B05").queue();
            message.addReaction("\u27A1").queue();
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
        if (page == Page.NEXT) {
            this.nextPage();
        } else {
            this.previousPage();
        }
    }

    public void delete(GiveawayBot bot) {
        bot.unRegisterListeners(this);
    }
}
