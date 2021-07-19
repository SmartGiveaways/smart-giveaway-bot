package pink.zak.giveawaybot.commands.discord.admin.subs.language;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.lang.model.Language;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;

public class SetLanguageSub extends SubCommand {

    public SetLanguageSub(GiveawayBot bot) {
        super(bot, "language", "set", false, false);
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        Language language = this.languageRegistry.getLanguage(event.getOption("language").getAsString());
        if (language == null) {
            this.langFor(server, Text.ADMIN_LANGUAGE_NOT_FOUND).to(event);
            return;
        }
        server.setLanguage(language.getIdentifier());
        this.langFor(server, Text.ADMIN_SET_LANGUAGE).to(event);
    }
}
