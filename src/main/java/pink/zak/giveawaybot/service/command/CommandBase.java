package pink.zak.giveawaybot.service.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.Permission;
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
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.enums.Language;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.cache.caches.Cache;
import pink.zak.giveawaybot.service.cache.CacheBuilder;
import pink.zak.giveawaybot.service.command.argument.ArgumentHandler;
import pink.zak.giveawaybot.service.command.argument.ArgumentType;
import pink.zak.giveawaybot.service.command.command.Command;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;
import pink.zak.giveawaybot.service.command.command.SubCommand;
import pink.zak.giveawaybot.service.types.UserUtils;
import pink.zak.giveawaybot.threads.ThreadFunction;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandBase extends ListenerAdapter {
    private final GiveawayBot bot;
    private final ServerCache serverCache;
    private final ExecutorService executor;
    private final Set<SimpleCommand> commands = Sets.newHashSet();
    private final Cache<Long, Long> commandCooldowns;
    private final LanguageRegistry languageRegistry;
    private final Permission[] requiredPermissions;
    private final AtomicInteger executions = new AtomicInteger();

    public CommandBase(GiveawayBot bot) {
        this.bot = bot;
        this.serverCache = bot.getServerCache();
        this.executor = bot.getThreadManager().getAsyncExecutor(ThreadFunction.COMMANDS);
        this.commandCooldowns = new CacheBuilder<Long, Long>().expireAfterAccess(1, TimeUnit.SECONDS).setControlling(bot).build();
        this.languageRegistry = bot.getLanguageRegistry();
        this.requiredPermissions = new Permission[]{Permission.MESSAGE_READ,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EXT_EMOJI,
                Permission.MESSAGE_ADD_REACTION,
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_MANAGE};
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
        Member selfMember = event.getGuild().getSelfMember();
        if (event.getAuthor().isBot() || !selfMember.hasPermission(event.getTextChannel(), Permission.MESSAGE_WRITE)) {
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
        this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
            if (!event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), this.requiredPermissions)) {
                this.languageRegistry.get(server, Text.BOT_DOESNT_HAVE_PERMISSIONS).to(event.getTextChannel());
                return;
            }
            if (server == null) {
                this.languageRegistry.get(Language.ENGLISH_UK, Text.FATAL_ERROR_LOADING_SERVER).to(event.getTextChannel());
                event.getTextChannel().sendMessage("There was a fatal error loading your server. Please join the support discord and scream at Zak.").queue();
                return;
            }
            CompletableFuture.runAsync(() -> {
                if (this.isOnCooldown(sender)) {
                    return;
                }
                for (SimpleCommand simpleCommand : this.commands) {
                    if (!simpleCommand.doesCommandMatch(commandName)) {
                        continue;
                    }
                    if (!this.hasAccess(server, simpleCommand, event.getMessage(), sender)) {
                        return;
                    }
                    Member member = event.getMember();
                    if (member == null) {
                        return;
                    }
                    if (!rawMessage.contains(" ")) {
                        this.commandCooldowns.set(member.getIdLong(), System.currentTimeMillis());
                        this.executeCommand(simpleCommand, sender, server, event, Lists.newArrayList());
                        return;
                    }
                    String message = rawMessage.split(prefix + commandName + " ")[1];
                    List<String> args = Lists.newArrayList(message.split(" "));
                    args.removeIf(String::isEmpty);

                    SubCommand subResult = null;
                    for (SubCommand subCommand : simpleCommand.getSubCommands()) {
                        if ((args.size() > subCommand.getArgumentsSize() && subCommand.isEndless() && subCommand.isEndlessMatch(args)) || (subCommand.getArgumentsSize() == args.size() && subCommand.isMatch(args))) {
                            subResult = subCommand;
                            break;
                        }
                    }
                    this.commandCooldowns.set(member.getIdLong(), System.currentTimeMillis());
                    if (subResult == null) {
                        this.executeCommand(simpleCommand, sender, server, event, args);
                        return;
                    }
                    if (this.hasAccess(server, subResult, event.getMessage(), sender)) {
                        this.executeCommand(subResult, sender, server, event, args);
                    }
                }
            }, this.executor).exceptionally(ex -> {
                GiveawayBot.getLogger().error("Error stemmed from CommandBase input {}", rawMessage, ex);
                return null;
            });
        }).exceptionally(ex -> {
            GiveawayBot.getLogger().error("Error stemmed from CommandBase input {}", rawMessage, ex);
            return null;
        });
    }

    private void executeCommand(Command command, Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        this.commandCooldowns.set(sender.getIdLong(), System.currentTimeMillis());
        this.executions.getAndIncrement();
        command.middleMan(sender, server, event, args);
    }

    private boolean hasAccess(Server server, Command simpleCommand, Message message, Member member) {
        if (simpleCommand.requiresManager() && !this.serverCache.get(member.getGuild().getIdLong()).join().canMemberManage(member)) {
            this.languageRegistry.get(server, Text.NO_PERMISSION).to(message.getTextChannel());
            return false;
        }
        return true;
    }

    /**
     * Returns whether a user is on command cooldown.
     * There is not any notification that they are, it's to maybe lessen some API limits.
     * Sending two messages within a second is... unnecessary at best.
     *
     * @param member The member to check the cooldown of.
     * @return Whether the user is on cooldown.
     */
    private boolean isOnCooldown(Member member) {
        return member.getIdLong() != 240721111174610945L && this.commandCooldowns.contains(member.getIdLong()) && System.currentTimeMillis() - this.commandCooldowns.getSync(member.getIdLong()) < 1000;
    }

    public Set<SimpleCommand> getCommands() {
        return this.commands;
    }

    public int retrieveExecutions() {
        return this.executions.getAndUpdate(previous -> 0);
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
                            GiveawayBot.getLogger().error("Error parsing MEMBER type from command. Input {}", userId, ex);
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
                            GiveawayBot.getLogger().error("Error parsing MEMBER type from command. Input {}", userId, ex);
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