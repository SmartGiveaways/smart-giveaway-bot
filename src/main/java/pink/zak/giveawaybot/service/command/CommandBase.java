package pink.zak.giveawaybot.service.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.service.cache.Cache;
import pink.zak.giveawaybot.service.cache.CacheBuilder;
import pink.zak.giveawaybot.service.command.argument.ArgumentHandler;
import pink.zak.giveawaybot.service.command.argument.ArgumentType;
import pink.zak.giveawaybot.service.command.command.Command;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CommandBase extends ListenerAdapter {
    private final GiveawayBot bot;
    private final ServerCache serverCache;
    private final Set<SimpleCommand> commands = Sets.newHashSet();
    private final Cache<Long, Long> commandCooldowns;
    private final Consumer<Throwable> deleteFailureThrowable;

    public CommandBase(GiveawayBot bot) {
        this.bot = bot;
        this.serverCache = bot.getServerCache();
        this.commandCooldowns = new CacheBuilder<Long, Long>().expireAfterAccess(1, TimeUnit.SECONDS).setControlling(bot).build();
        this.deleteFailureThrowable = bot.getDeleteFailureThrowable();
        this.registerArgumentTypes();
    }

    public void registerCommand(SimpleCommand command) {
        this.commands.add(command);
    }

    public CommandBase registerArgumentType(Class<?> clazz, ArgumentType<?> argumentType) {
        ArgumentHandler.register(clazz, argumentType);
        return this;
    }

    @Override
    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        String rawMessage = event.getMessage().getContentRaw();
        String prefix = this.bot.getPrefix();
        if (!rawMessage.startsWith(prefix)) {
            return;
        }
        Member sender = event.getMember();
        if (sender == null) {
            return;
        }
        String commandName = rawMessage.substring(1).split(" ")[0];
        for (SimpleCommand simpleCommand : this.commands) {
            if (!simpleCommand.getCommand().equalsIgnoreCase(commandName) && !simpleCommand.getAliases().contains(commandName)) {
                continue;
            }
            if (!this.hasAccess(simpleCommand, event.getMessage(), sender)) {
                return;
            }
            Member member = event.getMember();
            if (member == null) {
                return;
            }
            if (!rawMessage.contains(" ")) {
                if (this.isOnCooldown(event.getTextChannel(), event.getMessage(), sender)) {
                    return;
                }
                this.commandCooldowns.set(member.getIdLong(), System.currentTimeMillis());
                simpleCommand.middleMan(sender, event, Lists.newArrayList());
                return;
            }
            String message = rawMessage.split(prefix.concat(commandName).concat(" "))[1];
            List<String> args = Lists.newArrayList(message.split(" "));
            args.removeIf(String::isEmpty);

            SubCommand subResult = null;
            for (SubCommand subCommand : simpleCommand.getSubCommands()) {
                if ((args.size() > subCommand.getArgumentsSize() && subCommand.isEndless() && subCommand.isEndlessMatch(args)) || (subCommand.getArgumentsSize() == args.size() && subCommand.isMatch(args))) {
                    subResult = subCommand;
                    break;
                }
            }
            if (this.isOnCooldown(event.getTextChannel(), event.getMessage(), sender)) {
                return;
            }
            this.commandCooldowns.set(member.getIdLong(), System.currentTimeMillis());
            if (subResult == null) {
                simpleCommand.middleMan(sender, event, args);
                return;
            }
            if (this.hasAccess(subResult, event.getMessage(), sender))
                subResult.middleMan(sender, event, args);
        }
    }

    private boolean hasAccess(Command simpleCommand, Message message, Member member) {
        if (simpleCommand.requiresManager() && !this.serverCache.get(member.getGuild().getIdLong()).join().canMemberManage(member)) {
            message.getTextChannel().sendMessage(":x: No permission").queue(botReply -> {
                message.delete().queueAfter(10, TimeUnit.SECONDS, null, this.deleteFailureThrowable, this.bot.getThreadManager().getUpdaterExecutor());
                botReply.delete().queueAfter(10, TimeUnit.SECONDS, null, this.deleteFailureThrowable, this.bot.getThreadManager().getUpdaterExecutor());
            });
            return false;
        }
        return true;
    }

    private boolean isOnCooldown(TextChannel channel, Message message, Member member) {
        if (member.getIdLong() != 240721111174610945L && this.commandCooldowns.contains(member.getIdLong()) && System.currentTimeMillis() - this.commandCooldowns.getSync(member.getIdLong()) < 1000) {
            channel.sendMessage("<@" + member.getIdLong() + "> You must wait 1 second inbetween commands.").queue(botReply -> {
                message.delete().queueAfter(10, TimeUnit.SECONDS, null, this.deleteFailureThrowable, this.bot.getThreadManager().getUpdaterExecutor());
                botReply.delete().queueAfter(10, TimeUnit.SECONDS, null, this.deleteFailureThrowable, this.bot.getThreadManager().getUpdaterExecutor());
            });
            return true;
        }
        return false;
    }

    public Set<SimpleCommand> getCommands() {
        return this.commands;
    }

    private void registerArgumentTypes() {
        this.registerArgumentType(String.class, (string, guild) -> string)
                .registerArgumentType(Member.class, (string, guild) -> {
                    String id = string.length() == 21 ? string.substring(2, 20) : string.length() == 22 ? string.substring(3, 21) : null;
                    if (id == null) {
                        return null;
                    }
                    try {
                        return guild.retrieveMemberById(id).complete();
                    } catch (ErrorResponseException ex) {
                        if (ex.getErrorResponse() != ErrorResponse.UNKNOWN_USER && ex.getErrorResponse() != ErrorResponse.UNKNOWN_MEMBER) {
                            GiveawayBot.getLogger().error("Error parsing MEMBER type from command. Input {}", id, ex);
                        }
                        return null;
                    }
                })
                .registerArgumentType(User.class, (string, guild) -> {
                    String id = string.length() == 21 ? string.substring(2, 20) : string.length() == 22 ? string.substring(3, 21) : null;
                    if (id == null) {
                        return null;
                    }
                    try {
                        return this.bot.getShardManager().retrieveUserById(id).complete();
                    } catch (ErrorResponseException ex) {
                        if (ex.getErrorResponse() != ErrorResponse.UNKNOWN_USER) {
                            GiveawayBot.getLogger().error("Error parsing MEMBER type from command. Input {}", id, ex);
                        }
                        return null;
                    }
                })
                .registerArgumentType(TextChannel.class, (string, guild) -> {
                    String id = string.length() == 21 ? string.substring(2, 20) : string.length() == 22 ? string.substring(3, 21) : null;
                    if (id == null) {
                        return null;
                    }
                    return guild.getTextChannelById(id);
                })
                .registerArgumentType(Preset.class, (string, guild) -> Optional.ofNullable(this.serverCache.getSync(guild.getIdLong()).getPreset(string)))
                .registerArgumentType(Integer.class, (string, guild) -> NumberUtils.toInt(string, -1))
                .registerArgumentType(Long.class, (string, guild) -> NumberUtils.toLong(string, -1))
                .registerArgumentType(Double.class, (string, guild) -> NumberUtils.toDouble(string, -1))
                .registerArgumentType(Boolean.class, (string, guild) -> BooleanUtils.toBoolean(string));
    }
}