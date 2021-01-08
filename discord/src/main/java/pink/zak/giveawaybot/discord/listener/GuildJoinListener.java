package pink.zak.giveawaybot.discord.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.cache.ServerCache;
import pink.zak.giveawaybot.discord.lang.LanguageHelper;
import pink.zak.giveawaybot.discord.lang.LanguageRegistry;
import pink.zak.giveawaybot.discord.lang.enums.Text;
import pink.zak.giveawaybot.discord.models.Server;

/**
 * When the bot joins a guild
 */
public class GuildJoinListener extends ListenerAdapter {
    private final ServerCache serverCache;
    private final LanguageRegistry languageRegistry;

    public GuildJoinListener(GiveawayBot bot) {
        this.serverCache = bot.getServerCache();
        this.languageRegistry = bot.getLanguageRegistry();
    }

    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        TextChannel defaultChannel = guild.getDefaultChannel();
        Server server = this.serverCache.getOrInitialise(guild.getIdLong(), LanguageHelper.localeToId(guild.getLocale()));

        if (defaultChannel != null) {
            this.languageRegistry.get(server, Text.NEW_GUILD_JOINED, replacer -> replacer
                    .set("language", this.languageRegistry.getLanguage(server.getLanguage()).getName())
            ).to(defaultChannel);
        }
    }
}
