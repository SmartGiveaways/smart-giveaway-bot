package pink.zak.giveawaybot.commands.discord.ban;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.data.models.User;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;

public class UnbanCommand extends SimpleCommand {

    public UnbanCommand(GiveawayBot bot) {
        super(bot, "unban", true, false);
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        Member target = event.getOption("user").getAsMember();
        if (target == null) {
            this.langFor(server, Text.COULDNT_FIND_MEMBER).to(event, true);
            return;
        }
        if (target.getIdLong() == sender.getIdLong()) {
            this.langFor(server, Text.CANNOT_UNBAN_SELF).to(event, true);
            return;
        }
        User user = server.getUserCache().get(target.getIdLong());
        String userPlaceholder = target.getUser().getAsTag();
        server.getBannedUsers().remove(user.getId());
        if (user.isShadowBanned()) {
            this.langFor(server, Text.SHADOW_UNBANNED, replacer -> replacer.set("target", userPlaceholder)).to(event, true);
            user.setShadowBanned(false);
            return;
        }
        if (user.isBanned()) {
            this.langFor(server, Text.UNBANNED, replacer -> replacer.set("target", userPlaceholder)).to(event, true);
            server.getBannedUsers().remove(user.getId());
            user.setBanned(false);
            return;
        }
        this.langFor(server, Text.UNBAN_NOT_BANNED, replacer -> replacer.set("target", "<@" + user.getId() + ">")).to(event, true);
    }

    @Override
    protected CommandData createCommandData() {
        return new CommandData("unban", "Unban a user from giveaways")
            .addOption(OptionType.USER, "user", "Unban a user from giveaways", true);
    }
}

