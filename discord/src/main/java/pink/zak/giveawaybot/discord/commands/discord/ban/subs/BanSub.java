package pink.zak.giveawaybot.discord.commands.discord.ban.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.commands.discord.ban.BanCmdUtils;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.data.models.User;
import pink.zak.giveawaybot.discord.lang.Text;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;

import java.util.List;

public class BanSub extends SubCommand {
    private final BanCmdUtils cmdUtils;

    public BanSub(GiveawayBot bot, BanCmdUtils cmdUtils) {
        super(bot, true, false, false);
        this.cmdUtils = cmdUtils;

        this.addArgument(Member.class);
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        Member target = this.parseArgument(args, event.getGuild(), 0);
        TextChannel textChannel = event.getChannel();
        if (this.cmdUtils.handleAndIsNotEligible(server, sender, target, textChannel)) {
            return;
        }
        User user = server.getUserCache().get(target.getIdLong());
        String userPlaceholder = target.getUser().getAsTag();
        if (user.isBanned()) {
            this.langFor(server, Text.TARGET_ALREADY_BANNED, replacer -> replacer.set("target", userPlaceholder)).to(textChannel);
            return;
        }
        if (user.isShadowBanned()) {
            this.langFor(server, Text.CANNOT_BAN_IS_SHADOW_BANNED, replacer -> replacer.set("target", userPlaceholder)).to(textChannel);
            return;
        }
        server.getBannedUsers().add(user.getId());
        user.setBanned(true);
        this.langFor(server, Text.BANNED_SUCCESSFULLY, replacer -> replacer.set("target", userPlaceholder)).to(textChannel);
    }
}
