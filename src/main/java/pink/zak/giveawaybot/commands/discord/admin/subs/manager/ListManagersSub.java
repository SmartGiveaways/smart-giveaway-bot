package pink.zak.giveawaybot.commands.discord.admin.subs.manager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

import java.util.List;

public class ListManagersSub extends SubCommand {
    private final Palette palette;

    public ListManagersSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.addFlatWithAliases("roles", "list", "managers");

        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        if (server.getManagerRoles().isEmpty()) {
            this.langFor(server, Text.ADMIN_NO_MANAGERS).to(event.getChannel());
            return;
        }
        StringBuilder descriptionBuilder = new StringBuilder();
        for (long roleId : server.getManagerRoles()) {
            descriptionBuilder.append("<@&").append(roleId).append(">\n");
        }
        event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle(this.langFor(server, Text.ADMIN_MANAGER_LIST_TITLE).get())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).get())
                .setColor(this.palette.primary())
                .setDescription(descriptionBuilder.toString())
                .build()).queue();
    }
}
