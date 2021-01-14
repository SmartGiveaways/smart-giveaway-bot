package pink.zak.giveawaybot.discord.service.command.discord;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.lang.LanguageRegistry;
import pink.zak.giveawaybot.discord.lang.enums.Text;
import pink.zak.giveawaybot.discord.listener.message.GiveawayMessageListener;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.service.cache.CacheBuilder;
import pink.zak.giveawaybot.discord.service.cache.caches.Cache;
import pink.zak.giveawaybot.discord.service.command.discord.command.Command;
import pink.zak.giveawaybot.discord.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;
import pink.zak.giveawaybot.discord.service.command.global.CommandBackend;
import pink.zak.giveawaybot.discord.threads.ThreadFunction;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DiscordCommandBase extends CommandBackend implements GiveawayMessageListener {
    private final String prefix;
    private final ExecutorService executor;
    private final Set<SimpleCommand> commands = Sets.newHashSet();
    private final Cache<Long, Long> commandCooldowns;
    private final LanguageRegistry languageRegistry;
    private final Set<Permission> requiredPermissions;
    private final AtomicInteger executions = new AtomicInteger();

    public DiscordCommandBase(GiveawayBot bot) {
        super(bot);
        this.prefix = bot.getPrefix();
        this.executor = bot.getThreadManager().getAsyncExecutor(ThreadFunction.GENERAL);
        this.commandCooldowns = new CacheBuilder<Long, Long>().expireAfterAccess(1, TimeUnit.SECONDS).setControlling(bot).build();
        this.languageRegistry = bot.getLanguageRegistry();
        this.requiredPermissions = Sets.newHashSet(Permission.MESSAGE_READ,
                Permission.MESSAGE_EXT_EMOJI,
                Permission.MESSAGE_ADD_REACTION,
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_MANAGE);
    }

    public void registerCommand(SimpleCommand command) {
        this.commands.add(command);
    }

    public void onExecute(Server server, GuildMessageReceivedEvent event) {
        Member selfMember = event.getGuild().getSelfMember();
        if (event.getAuthor().isBot() || GiveawayBot.isLocked() || !selfMember.hasPermission(event.getChannel(), Permission.MESSAGE_WRITE)) {
            return;
        }
        String rawMessage = event.getMessage().getContentRaw();
        if (!rawMessage.startsWith(this.prefix)) {
            return;
        }
        Member sender = event.getMember();
        if (sender == null) {
            return;
        }
        String commandName = rawMessage.substring(1).split(" ")[0];
        TextChannel channel = event.getChannel();
        if (!this.handleBotPermsCheck(server, channel, selfMember)) {
            return;
        }
        if (server == null) {
            this.languageRegistry.fallback(Text.FATAL_ERROR_LOADING_SERVER).to(channel);
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
                if (!this.memberHasAccess(server, simpleCommand, channel, sender)) {
                    return;
                }
                if (!server.isPremium() && simpleCommand.requiresPremium()) {
                    this.languageRegistry.get(server, Text.COMMAND_REQUIRES_PREMIUM).to(channel);
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
                String message = rawMessage.split(this.prefix + commandName + " ")[1];
                List<String> args = Lists.newArrayList(message.split(" "));
                args.removeIf(String::isEmpty);

                SubCommand subResult = null;
                for (SubCommand subCommand : simpleCommand.getSubCommands()) {
                    if ((args.size() > subCommand.argsSize() && subCommand.isEndless() && subCommand.isEndlessMatch(args)) || (subCommand.argsSize() == args.size() && subCommand.isMatch(args))) {
                        subResult = subCommand;
                        break;
                    }
                }
                this.commandCooldowns.set(member.getIdLong(), System.currentTimeMillis());
                if (subResult == null) {
                    this.executeCommand(simpleCommand, sender, server, event, args);
                    return;
                }
                if (!server.isPremium() && subResult.requiresPremium()) {
                    this.languageRegistry.get(server, Text.COMMAND_REQUIRES_PREMIUM).to(channel);
                    return;
                }
                if (this.memberHasAccess(server, subResult, channel, sender)) {
                    this.executeCommand(subResult, sender, server, event, args);
                }
            }
        }, this.executor).exceptionally(ex -> {
            GiveawayBot.logger().error("Error from CommandBase input {}", rawMessage, ex);
            return null;
        });
    }

    /**
     * @return whether the bot has perms and can proceed
     */
    private boolean handleBotPermsCheck(Server server, TextChannel channel, Member selfMember) {
        if (!selfMember.hasPermission(channel, this.requiredPermissions)) {
            Set<Permission> missingPerms = Sets.newHashSet(this.requiredPermissions);
            missingPerms.removeAll(selfMember.getPermissions(channel));
            String missingPermsString = missingPerms.stream().map(Permission::getName).map(str -> "`" + str + "`").collect(Collectors.joining(", "));
            this.languageRegistry.get(server, missingPerms.size() > 1 ? Text.BOT_MISSING_PERMISSIONS_SPECIFIC : Text.BOT_MISSING_PERMISSION_SPECIFIC,
                    replacer -> replacer.set("permission", missingPermsString)).to(channel);
            return false;
        }
        return true;
    }

    private void executeCommand(Command command, Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        this.commandCooldowns.set(sender.getIdLong(), System.currentTimeMillis());
        this.executions.getAndIncrement();
        command.middleMan(sender, server, event, args);
    }

    private boolean memberHasAccess(Server server, Command simpleCommand, TextChannel channel, Member member) {
        if (simpleCommand.requiresManager() && !server.canMemberManage(member)) {
            this.languageRegistry.get(server, Text.NO_PERMISSION).to(channel);
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
        return member.getIdLong() != 240721111174610945L && this.commandCooldowns.contains(member.getIdLong()) && System.currentTimeMillis() - this.commandCooldowns.get(member.getIdLong()) < 1000;
    }

    public Set<SimpleCommand> getCommands() {
        return this.commands;
    }

    public int retrieveExecutions() {
        return this.executions.getAndUpdate(previous -> 0);
    }
}