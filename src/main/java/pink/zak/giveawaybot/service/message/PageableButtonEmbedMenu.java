package pink.zak.giveawaybot.service.message;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.service.BotConstants;
import pink.zak.giveawaybot.service.colour.Palette;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class PageableButtonEmbedMenu extends PageableMenu {
    protected final LanguageRegistry languageRegistry;
    protected final Palette palette;
    private final GiveawayBot bot;

    private final Map<Integer, MessageEmbed> cachedPages = Maps.newConcurrentMap();
    private InteractionHook message;
    protected final Server server;

    private ScheduledFuture<?> scheduledFuture;

    private Button forwardButton;
    private Button backButton;

    protected PageableButtonEmbedMenu(GiveawayBot bot, Server server) {
        this.languageRegistry = bot.getLanguageRegistry();
        this.palette = bot.getDefaults().getPalette();
        this.bot = bot;

        this.server = server;
    }

    public void sendInitialMessage(SlashCommandEvent event, boolean ephemeral) {
        MessageEmbed embed = this.createPage(super.currentPage.get());
        this.cachedPages.put(super.currentPage.get(), embed);

        if (super.maxPage > 1) {
            this.backButton = Button.primary(UUID.randomUUID().toString(), BotConstants.BACK_EMOJI);
            this.forwardButton = Button.primary(UUID.randomUUID().toString(), BotConstants.FORWARD_EMOJI);

            this.bot.getButtonRegistry().registerButton(this.backButton, this::previousPage)
                .registerButton(this.forwardButton, this::nextPage);
        }

        ReplyAction replyAction = event.replyEmbeds(embed)
            .setEphemeral(ephemeral);

        if (this.forwardButton != null)
            replyAction.addActionRow(this.forwardButton);

        replyAction.queue(sentMessage -> {
            this.message = sentMessage;

            if (super.maxPage > 1)
                this.scheduleDeletion();
        });
    }

    public abstract MessageEmbed createPage(int page);

    public void nextPage(ButtonClickEvent event) {
        int initialPage = this.currentPage.get();
        int newPage = super.nextPage();

        if (initialPage != newPage)
            this.drawPage(this.currentPage.get(), event);
    }

    public void previousPage(ButtonClickEvent event) {
        int initialPage = this.currentPage.get();
        int newPage = super.previousPage();

        if (initialPage != newPage)
            this.drawPage(this.currentPage.get(), event);
    }

    public void drawPage(int page, ButtonClickEvent event) {
        if (page > this.maxPage) {
            return;
        }
        MessageEmbed embed = this.cachedPages.computeIfAbsent(page, this::createPage);

        Set<Button> buttons = Sets.newHashSet();
        if (page < this.maxPage)
            buttons.add(this.forwardButton);
        if (page > 1)
            buttons.add(this.backButton);
        event.editMessageEmbeds(embed)
            .setActionRow(buttons).queue();
    }

    @Override
    public void drawPage(int page) {

    }

    public void scheduleDeletion() {
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(false);
        }
        this.scheduledFuture = this.bot.getThreadManager().getScheduler().schedule(() -> this.bot.getButtonRegistry().deregisterButtons(this.forwardButton, this.backButton), 1, TimeUnit.MINUTES);
    }
}
