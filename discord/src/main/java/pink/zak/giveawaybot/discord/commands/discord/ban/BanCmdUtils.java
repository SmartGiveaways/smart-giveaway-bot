package pink.zak.giveawaybot.discord.commands.discord.ban;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.lang.LanguageRegistry;
import pink.zak.giveawaybot.discord.lang.enums.Text;
import pink.zak.giveawaybot.discord.models.Server;


public class BanCmdUtils {
    private final LanguageRegistry languageRegistry;

    public BanCmdUtils(GiveawayBot bot) {
        this.languageRegistry = bot.getLanguageRegistry();
    }

    public boolean handleAndIsNotEligible(Server server, Member sender, Member target, TextChannel textChannel) {
        if (target == null) {
            this.languageRegistry.get(server, Text.COULDNT_FIND_MEMBER).to(textChannel);
            return true;
        }
        if (target.getUser().isBot() && textChannel.getGuild().getSelfMember().equals(target)) {
            this.languageRegistry.get(server, Text.CANNOT_BAN_THE_BOT).to(textChannel);
            return true;
        }
        if (target.getIdLong() == sender.getIdLong()) {
            this.languageRegistry.get(server, Text.CANNOT_BAN_SELF).to(textChannel);
            return true;
        }
        if (server.canMemberManage(target)) {
            this.languageRegistry.get(server, Text.NOT_ENOUGH_PERMISSIONS_BAN, replacer -> replacer.set("target", target.getAsMention())).to(textChannel);
            return true;
        }
        return false;
    }
}
