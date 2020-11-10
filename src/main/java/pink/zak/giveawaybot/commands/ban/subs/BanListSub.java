package pink.zak.giveawaybot.commands.ban.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public class BanListSub extends SubCommand {
    private final Palette palette;

    public BanListSub(GiveawayBot bot) {
        super(bot, true);
        this.addFlat("list");

        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        StringBuilder descriptionBuilder = new StringBuilder();
        event.getTextChannel().sendMessage(new EmbedBuilder()
                .setTitle(this.langFor(server, Text.BAN_LIST_EMBED_TITLE).get())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).get())
                .setDescription(descriptionBuilder.toString())
                .setColor(this.palette.primary())
                .build()).queue();
    }
}
