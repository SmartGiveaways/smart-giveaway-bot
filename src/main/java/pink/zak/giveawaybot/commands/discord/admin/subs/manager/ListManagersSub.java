package pink.zak.giveawaybot.commands.discord.admin.subs.manager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.BotSubCommand;

public class ListManagersSub extends BotSubCommand {
    private final Palette palette;

    public ListManagersSub(GiveawayBot bot) {
        super(bot, "manager", "list", false, false);

        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        if (server.getManagerRoles().isEmpty()) {
            this.langFor(server, Text.ADMIN_NO_MANAGERS).to(event, true);
            return;
        }
        StringBuilder descriptionBuilder = new StringBuilder();
        for (long roleId : server.getManagerRoles()) {
            descriptionBuilder.append("<@&").append(roleId).append(">\n");
        }
        event.replyEmbeds(new EmbedBuilder()
            .setTitle(this.langFor(server, Text.ADMIN_MANAGER_LIST_TITLE).toString())
            .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).toString())
            .setColor(this.palette.primary())
            .setDescription(descriptionBuilder.toString())
            .build())
            .setEphemeral(true).queue();
    }
}
