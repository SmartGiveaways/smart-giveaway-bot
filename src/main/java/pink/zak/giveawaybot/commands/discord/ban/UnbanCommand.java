package pink.zak.giveawaybot.commands.discord.ban;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;
import pink.zak.giveawaybot.service.types.UserUtils;

import java.util.List;

public class UnbanCommand extends SimpleCommand {

    public UnbanCommand(GiveawayBot bot) {
        super(bot, "gunban", true, false);
        this.setAliases("gpardon");

        this.setSubCommands(new UnbanSub(bot));
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        this.langFor(server, Text.UNBAN_HELP).to(event.getChannel());
    }

    private class UnbanSub extends SubCommand {

        public UnbanSub(GiveawayBot bot) {
            super(bot, true, false, false);

            this.addArgument(Member.class);
        }

        @Override
        public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
            Member target = this.parseArgument(args, event.getGuild(), 0);
            TextChannel textChannel = event.getChannel();
            if (target == null) {
                this.langFor(server, Text.COULDNT_FIND_MEMBER).to(textChannel);
                return;
            }
            if (target.getIdLong() == sender.getIdLong()) {
                this.langFor(server, Text.CANNOT_UNBAN_SELF).to(textChannel);
                return;
            }
            server.getUserCache().get(target.getIdLong()).thenAccept(user -> {
                String userPlaceholder = UserUtils.getNameDiscrim(target);
                if (user.isShadowBanned()) {
                    this.langFor(server, Text.SHADOW_UNBANNED, replacer -> replacer.set("target", userPlaceholder)).to(textChannel);
                    user.unShadowBan();
                    return;
                }
                if (user.isBanned()) {
                    this.langFor(server, Text.UNBANNED, replacer -> replacer.set("target", userPlaceholder)).to(textChannel);
                    server.getBannedUsers().remove(user.id());
                    user.unBan();
                    return;
                }
                this.langFor(server, Text.UNBAN_NOT_BANNED, replacer -> replacer.set("target", user)).to(textChannel);
            });
        }
    }
}

