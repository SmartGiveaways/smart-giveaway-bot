package pink.zak.giveawaybot.commands.discord.ban;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.data.models.User;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.SlashCommandUtils;
import pink.zak.giveawaybot.service.command.discord.command.BotCommand;

public class BanCommand extends BotCommand {
    private final BanCmdUtils cmdUtils = new BanCmdUtils(this.bot);

    public BanCommand(GiveawayBot bot) {
        super(bot, "ban", true, false);
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        Member target = event.getOption("user").getAsMember();
        boolean shadowBan = SlashCommandUtils.hasOption(event, "shadow") && event.getOption("shadow").getAsBoolean();
        if (this.cmdUtils.handleAndIsNotEligible(server, sender, target, event)) {
            return;
        }
        User user = server.getUserCache().get(target.getIdLong());
        String userPlaceholder = target.getUser().getAsMention();
        if (user.isBanned()) {
            this.langFor(server, Text.TARGET_ALREADY_BANNED, replacer -> replacer.set("target", userPlaceholder)).to(event, true);
            return;
        }
        if (user.isShadowBanned()) {
            this.langFor(server, Text.TARGET_ALREADY_SHADOW_BANNED, replacer -> replacer.set("target", userPlaceholder)).to(event, true);
            return;
        }
        server.getBannedUsers().add(user.getId());
        if (shadowBan) {
            user.setShadowBanned(true);
            this.langFor(server, Text.SHADOW_BANNED_SUCCESSFULLY, replacer -> replacer.set("target", userPlaceholder)).to(event, true);
        } else {
            user.setBanned(true);
            this.langFor(server, Text.BANNED_SUCCESSFULLY, replacer -> replacer.set("target", userPlaceholder)).to(event, true);
        }
    }

    @Override
    protected CommandData createCommandData() {
        return new CommandData("ban", "Ban a user from giveaways")
            .addOption(OptionType.USER, "user", "Ban a user from giveaways", true)
            .addOption(OptionType.BOOLEAN, "shadow", "If true, the user will have no way to tell they are banned", false);
    }
}
