package pink.zak.giveawaybot.commands.ban;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Server;


public class BanCmdUtils {
    private final LanguageRegistry languageRegistry;

    public BanCmdUtils(GiveawayBot bot) {
        this.languageRegistry = bot.getLanguageRegistry();
    }

    public boolean handleAndIsEligible(Server server, Member sender, Member target, TextChannel textChannel) {
        if (target == null) {
            this.languageRegistry.get(server, Text.COULDNT_FIND_MEMBER).to(textChannel);
            return false;
        }
        if (target.getUser().isBot() && textChannel.getGuild().getSelfMember().equals(target)) {
            this.languageRegistry.get(server, Text.CANNOT_BAN_THE_BOT).to(textChannel);
            return false;
        }
        if (target.getIdLong() == sender.getIdLong()) {
            this.languageRegistry.get(server, Text.CANNOT_BAN_SELF).to(textChannel);
            return false;
        }
        if (server.canMemberManage(target)) {
            this.languageRegistry.get(server, Text.NOT_ENOUGH_PERMISSIONS_BAN, replacer -> replacer.set("target", target.getAsMention())).to(textChannel);
            return false;
        }
        return true;
    }
}
