package pink.zak.giveawaybot.commands.discord.premium;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.service.time.Time;

public class PremiumCommand extends SimpleCommand {
    private final Palette palette;

    public PremiumCommand(GiveawayBot bot) {
        super(bot, "premium", false, false);

        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        event.replyEmbeds(new EmbedBuilder()
            .setTitle(this.langFor(server, Text.PREMIUM_EMBED_TITLE).toString())
            .setFooter(this.langFor(server, Text.PREMIUM_EMBED_FOOTER).toString())
            .setColor(this.palette.primary())
            .setDescription(this.langFor(server, server.isPremium() ? Text.PREMIUM_EMBED_DESCRIPTION_PURCHASED : Text.PREMIUM_EMBED_DESCRIPTION_NOT_PURCHASED,
                replacer -> server.isPremium() ? replacer.set("expiry", Time.format(server.getTimeToPremiumExpiry())) : replacer).toString())
            .build())
            .setEphemeral(true).queue();
    }

    @Override
    protected CommandData createCommandData() {
        return new CommandData("premium", "Get info about premium or your premium server status.");
    }
}
