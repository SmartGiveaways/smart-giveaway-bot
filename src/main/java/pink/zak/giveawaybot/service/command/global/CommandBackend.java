package pink.zak.giveawaybot.service.command.global;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.cache.ServerCache;
import pink.zak.giveawaybot.data.models.Preset;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.model.Language;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.command.global.argument.ArgumentType;
import pink.zak.giveawaybot.service.command.global.argument.ArgumentTypeUtils;
import pink.zak.giveawaybot.service.types.BooleanUtils;
import pink.zak.giveawaybot.service.types.NumberUtils;
import pink.zak.giveawaybot.service.types.UserUtils;

public abstract class CommandBackend {
    private final GiveawayBot bot;
    private final ServerCache serverCache;
    private final LanguageRegistry languageRegistry;

    protected CommandBackend(GiveawayBot bot) {
        this.bot = bot;
        this.serverCache = bot.getServerCache();
        this.languageRegistry = bot.getLanguageRegistry();

        this.registerArgumentTypes();
    }

    protected CommandBackend registerArgumentType(Class<?> clazz, ArgumentType<?> argumentType) {
        ArgumentTypeUtils.register(clazz, argumentType);
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
                            JdaBot.LOGGER.error("Error parsing MEMBER type from command. Input {}", userId, ex);
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
                            JdaBot.LOGGER.error("Error parsing MEMBER type from command. Input {}", userId, ex);
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
