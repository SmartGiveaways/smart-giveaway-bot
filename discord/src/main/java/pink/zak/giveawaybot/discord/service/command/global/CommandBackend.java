package pink.zak.giveawaybot.discord.service.command.global;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.cache.ServerCache;
import pink.zak.giveawaybot.discord.lang.LanguageRegistry;
import pink.zak.giveawaybot.discord.lang.model.Language;
import pink.zak.giveawaybot.discord.models.Preset;
import pink.zak.giveawaybot.discord.service.command.global.argument.ArgumentHandler;
import pink.zak.giveawaybot.discord.service.command.global.argument.ArgumentType;
import pink.zak.giveawaybot.discord.service.types.BooleanUtils;
import pink.zak.giveawaybot.discord.service.types.NumberUtils;
import pink.zak.giveawaybot.discord.service.types.UserUtils;

public abstract class CommandBackend {
    private final GiveawayBot bot;
    private final ServerCache serverCache;
    private final LanguageRegistry languageRegistry;

    public CommandBackend(GiveawayBot bot) {
        this.bot = bot;
        this.serverCache = bot.getServerCache();
        this.languageRegistry = bot.getLanguageRegistry();

        this.registerArgumentTypes();
    }

    public CommandBackend registerArgumentType(Class<?> clazz, ArgumentType<?> argumentType) {
        ArgumentHandler.register(clazz, argumentType);
        return this;
    }

    private void registerArgumentTypes() {
        this.registerArgumentType(String.class, (string, guild) -> string)
                .registerArgumentType(Member.class, (string, guild) -> {
                    long userId = UserUtils.parseIdInput(string);
                    if (userId == -1) {
                        return null;
                    }
                    try {
                        return guild.retrieveMemberById(userId).complete();
                    } catch (ErrorResponseException ex) {
                        if (ex.getErrorResponse() != ErrorResponse.UNKNOWN_USER && ex.getErrorResponse() != ErrorResponse.UNKNOWN_MEMBER) {
                            GiveawayBot.logger().error("Error parsing MEMBER type from command. Input {}", userId, ex);
                        }
                        return null;
                    }
                })
                .registerArgumentType(User.class, (string, guild) -> {
                    long userId = UserUtils.parseIdInput(string);
                    if (userId == -1) {
                        return null;
                    }
                    try {
                        return this.bot.getShardManager().retrieveUserById(userId).complete();
                    } catch (ErrorResponseException ex) {
                        if (ex.getErrorResponse() != ErrorResponse.UNKNOWN_USER) {
                            GiveawayBot.logger().error("Error parsing MEMBER type from command. Input {}", userId, ex);
                        }
                        return null;
                    }
                })
                .registerArgumentType(TextChannel.class, (string, guild) -> {
                    long channelId = UserUtils.parseIdInput(string);
                    if (channelId == -1) {
                        return null;
                    }
                    return guild.getTextChannelById(channelId);
                })
                .registerArgumentType(Role.class, (string, guild) -> {
                    long roleId = UserUtils.parseIdInput(string);
                    if (roleId == -1) {
                        return null;
                    }
                    return guild.getRoleById(roleId);
                })
                .registerArgumentType(Language.class, (string, guild) -> this.languageRegistry.getLanguage(string))
                .registerArgumentType(Preset.class, (string, guild) -> this.serverCache.get(guild.getIdLong()).getPreset(string.toLowerCase()))
                .registerArgumentType(Integer.class, (string, guild) -> NumberUtils.parseInt(string, -1))
                .registerArgumentType(Long.class, (string, guild) -> NumberUtils.parseLong(string, -1))
                .registerArgumentType(Double.class, (string, guild) -> NumberUtils.parseDouble(string, -1))
                .registerArgumentType(Boolean.class, (string, guild) -> BooleanUtils.parseBoolean(string));
    }
}
