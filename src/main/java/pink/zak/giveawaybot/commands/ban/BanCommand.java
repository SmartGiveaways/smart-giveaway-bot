package pink.zak.giveawaybot.commands.ban;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;
import pink.zak.giveawaybot.service.command.command.SubCommand;
import pink.zak.giveawaybot.service.types.UserUtils;

import java.util.List;

public class BanCommand extends SimpleCommand {

    public BanCommand(GiveawayBot bot) {
        super(bot, true, "ban");

        this.setSubCommands(new BanSub(bot));
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(">ban <user> - Bans a user from giveaways.").queue();
    }

    private class BanSub extends SubCommand {
        private final ServerCache serverCache;

        public BanSub(GiveawayBot bot) {
            super(bot, true);
            this.serverCache = bot.getServerCache();

            this.addArgument(Member.class);
        }

        @Override
        public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
            Member target = this.parseArgument(args, event.getGuild(), 0);
            if (target == null) {
                event.getChannel().sendMessage(":x: Couldn't find that member :worried:").queue();
                return;
            }
            if (target.getIdLong() == sender.getIdLong()) {
                event.getChannel().sendMessage(":x: You can't ban yourself dummy.").queue();
                return;
            }
            this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
                if (server.canMemberManage(target)) {
                    event.getChannel().sendMessage(":x: You're not high enough in the god complex to ban " + target.getAsMention()).queue();
                    return;
                }
                server.getUserCache().get(target.getIdLong()).thenAccept(user -> {
                    if (user.isBanned()) {
                        event.getChannel().sendMessage(":x: " + UserUtils.getNameDiscrim(target) + " is already banned.").queue();
                        return;
                    }
                    if (user.isShadowBanned()) {
                        event.getChannel().sendMessage(":x: " + UserUtils.getNameDiscrim(target) + " is shadow banned. Unban them first if you want to overwrite this.").queue();
                        return;
                    }
                    user.ban();
                    event.getChannel().sendMessage(":white_check_mark: banned " + target.getEffectiveName() + "#" + target.getUser().getDiscriminator() + ".").queue();
                });
            });
        }
    }
}
