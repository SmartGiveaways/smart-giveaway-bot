package pink.zak.giveawaybot.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.cache.ServerCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.LanguageHelper;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.service.SlashCommandUtils;
import pink.zak.giveawaybot.service.command.discord.DiscordCommandBase;

/**
 * When the bot joins a guild
 */
public class GuildJoinListener extends ListenerAdapter {
    private final ServerCache serverCache;
    private final LanguageRegistry languageRegistry;
    private final DiscordCommandBase discordCommandBase;

    public GuildJoinListener(GiveawayBot bot) {
        this.serverCache = bot.getServerCache();
        this.languageRegistry = bot.getLanguageRegistry();
        this.discordCommandBase = bot.getDiscordCommandBase();
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        TextChannel defaultChannel = guild.getDefaultChannel();
        Server server = this.serverCache.getOrInitialise(guild.getIdLong(), LanguageHelper.localeToId(guild.getLocale()));

        SlashCommandUtils.updatePrivileges(event.getGuild(), server, this.discordCommandBase);

        if (defaultChannel != null) {
            this.languageRegistry.get(server, Text.NEW_GUILD_JOINED, replacer -> replacer
                    .set("language", this.languageRegistry.getLanguage(server.getLanguage()).getName())
            ).to(defaultChannel);
        }
    }
}
