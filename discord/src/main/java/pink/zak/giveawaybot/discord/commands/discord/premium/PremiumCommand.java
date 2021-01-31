package pink.zak.giveawaybot.discord.commands.discord.premium;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.lang.Text;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.service.colour.Palette;
import pink.zak.giveawaybot.discord.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.discord.service.time.Time;

import java.util.List;

public class PremiumCommand extends SimpleCommand {
    private final Palette palette;

    public PremiumCommand(GiveawayBot bot) {
        super(bot, "premium", false, false);

        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle(this.langFor(server, Text.PREMIUM_EMBED_TITLE).get())
                .setFooter(this.langFor(server, Text.PREMIUM_EMBED_FOOTER).get())
                .setColor(this.palette.primary())
                .setDescription(this.langFor(server, server.isPremium() ? Text.PREMIUM_EMBED_DESCRIPTION_PURCHASED : Text.PREMIUM_EMBED_DESCRIPTION_NOT_PURCHASED,
                        replacer -> server.isPremium() ? replacer.set("expiry", Time.format(server.getTimeToPremiumExpiry())) : replacer).get())
                .build()).queue();
    }
}
