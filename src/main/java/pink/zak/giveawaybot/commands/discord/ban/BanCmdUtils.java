package pink.zak.giveawaybot.commands.discord.ban;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.Text;


public class BanCmdUtils {
    private final LanguageRegistry languageRegistry;

    public BanCmdUtils(GiveawayBot bot) {
        this.languageRegistry = bot.getLanguageRegistry();
    }

    public boolean handleAndIsNotEligible(Server server, Member sender, Member target, SlashCommandEvent event) {
        if (target == null) {
            this.languageRegistry.get(server, Text.COULDNT_FIND_MEMBER).to(event);
            return true;
        }
        if (target.getUser().isBot() && event.getGuild().getSelfMember().equals(target)) {
            this.languageRegistry.get(server, Text.CANNOT_BAN_THE_BOT).to(event);
            return true;
        }
        if (target.getIdLong() == sender.getIdLong()) {
            this.languageRegistry.get(server, Text.CANNOT_BAN_SELF).to(event);
            return true;
        }
        if (server.canMemberManage(target)) {
            this.languageRegistry.get(server, Text.NOT_ENOUGH_PERMISSIONS_BAN, replacer -> replacer.set("target", target.getAsMention())).to(event);
            return true;
        }
        return false;
    }
}
