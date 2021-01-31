package pink.zak.giveawaybot.discord.commands.discord.admin.subs.language;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.lang.Text;
import pink.zak.giveawaybot.discord.lang.model.Language;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;

import java.util.List;

public class SetLanguageSub extends SubCommand {

    public SetLanguageSub(GiveawayBot bot) {
        super(bot, true, false, false);
        this.addFlatWithAliases("language", "languages", "lang", "langs");
        this.addArgument(Language.class); // The language
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        Language language = this.parseArgument(args, event.getGuild(), 1);
        if (language == null) {
            this.langFor(server, Text.ADMIN_LANGUAGE_NOT_FOUND).to(event.getChannel());
            return;
        }
        server.setLanguage(language.getIdentifier());
        this.langFor(server, Text.ADMIN_SET_LANGUAGE).to(event.getChannel());
    }
}
