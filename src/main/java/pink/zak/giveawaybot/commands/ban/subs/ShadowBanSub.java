package pink.zak.giveawaybot.commands.ban.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.ban.BanCmdUtils;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.command.SubCommand;
import pink.zak.giveawaybot.service.types.UserUtils;

import java.util.List;

public class ShadowBanSub extends SubCommand {
    private final BanCmdUtils cmdUtils;

    public ShadowBanSub(GiveawayBot bot, BanCmdUtils cmdUtils) {
        super(bot, true);
        this.cmdUtils = cmdUtils;

        this.addArgument(String.class, string -> string.equalsIgnoreCase("-s") || string.equalsIgnoreCase("shadow") || string.equalsIgnoreCase("silent"));
        this.addArgument(Member.class);
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        String shadowInput = this.parseArgument(args, event.getGuild(), 0);
        if (!this.matchesShadow(shadowInput)) {
            return;
        }
        Member target = this.parseArgument(args, event.getGuild(), 1);
        TextChannel textChannel = event.getTextChannel();
        if (!this.cmdUtils.handleAndIsEligible(server, sender, target, textChannel)) {
            return;
        }
        server.getUserCache().get(target.getIdLong()).thenAccept(user -> {
            String userPlaceholder = UserUtils.getNameDiscrim(target);
            if (user.isShadowBanned()) {
                this.langFor(server, Text.TARGET_ALREADY_SHADOW_BANNED, replacer -> replacer.set("target", userPlaceholder)).to(textChannel);
                return;
            }
            if (user.isBanned()) {
                this.langFor(server, Text.CANNOT_BAN_IS_BANNED, replacer -> replacer.set("target", userPlaceholder)).to(textChannel);
                return;
            }
            user.shadowBan();
            this.langFor(server, Text.SHADOW_BANNED_SUCCESSFULLY, replacer -> replacer.set("target", userPlaceholder)).to(textChannel);
        });
    }

    private boolean matchesShadow(String input) {
        String lowerInput = input.toLowerCase();
        return lowerInput.equals("-s") || lowerInput.equals("shadow") || lowerInput.equals("silent");
    }
}
