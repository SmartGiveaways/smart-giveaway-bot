package pink.zak.giveawaybot.commands.ban;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public class UnbanCommand extends SimpleCommand {

    public UnbanCommand(GiveawayBot bot) {
        super(bot, true, "unban");
        this.setAliases("pardon");

        this.setSubCommands(new UnbanSub(bot));
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(">unban <user> - Unbans a user if they are banned.").queue();

    }

    private class UnbanSub extends SubCommand {
        private final ServerCache serverCache;

        public UnbanSub(GiveawayBot bot) {
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
                event.getChannel().sendMessage(":x: Oi, get someone else to do it for you.").queue();
                return;
            }
            this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
                server.getUserCache().get(target.getIdLong()).thenAccept(user -> {
                    if (user.isShadowBanned()) {
                        event.getChannel().sendMessage(":white_check_mark: " + target.getAsMention() + " is no longer shadow banned.").queue();
                        user.unShadowBan();
                        return;
                    }
                    if (user.isBanned()) {
                        event.getChannel().sendMessage(":white_check_mark: " + target.getAsMention() + " is no longer banned.").queue();
                        user.unBan();
                        return;
                    }
                    event.getChannel().sendMessage(":x: Slight problem... they're not banned or shadow banned. You only pardon the guilty.").queue();
                });
            });
        }
    }
}

