package pink.zak.giveawaybot.service.command.discord;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.Defaults;
import pink.zak.giveawaybot.data.cache.ServerCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.LanguageRegistry;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.listener.slash.SlashCommandListener;
import pink.zak.giveawaybot.service.bot.JdaBot;
import pink.zak.giveawaybot.service.command.discord.command.Command;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.service.command.discord.command.SubCommand;
import pink.zak.giveawaybot.service.command.global.CommandBackend;
import pink.zak.giveawaybot.service.types.UserUtils;
import pink.zak.giveawaybot.threads.ThreadFunction;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DiscordCommandBase extends CommandBackend implements SlashCommandListener {
    private final JDA jda;
    private final ExecutorService executor;
    private final LanguageRegistry languageRegistry;
    private final Set<Permission> requiredPermissions;
    private final AtomicInteger executions = new AtomicInteger();

    private final Map<String, SimpleCommand> commands = Maps.newHashMap();
    private final Map<Long, SimpleCommand> slashCommands = Maps.newHashMap();

    public DiscordCommandBase(GiveawayBot bot) {
        super(bot);
        this.jda = bot.getJda();
        this.executor = bot.getThreadManager().getAsyncExecutor(ThreadFunction.GENERAL);
        this.languageRegistry = bot.getLanguageRegistry();
        this.requiredPermissions = Sets.newHashSet(Defaults.requiredPermissions);
        this.requiredPermissions.remove(Permission.MESSAGE_WRITE); // message write is checked early
    }

    public void registerCommand(SimpleCommand command) {
        this.commands.put(command.getCommandId(), command);
    }

    public void init() {
        // load existing commands so we dont have to update every time
        Set<CommandData> createdData = this.commands.values()
                .stream()
                .map(SimpleCommand::getCommandData)
                .collect(Collectors.toSet());
        System.out.println("Created data " + createdData);

        List<net.dv8tion.jda.api.interactions.commands.Command> commands = this.jda.getGuildById(751886048623067186L).updateCommands().addCommands(createdData).complete();
        System.out.println("Commands " + commands);
        //List<net.dv8tion.jda.api.interactions.commands.Command> commands = this.jda.updateCommands().addCommands(createdData).complete();

        commands.forEach(command -> {
            SimpleCommand matchedCommand = this.commands.get(command.getName());
            this.slashCommands.put(command.getIdLong(), matchedCommand);
            JdaBot.LOGGER.info("Bound command {} to ID {}", matchedCommand.getCommandId(), command.getIdLong());
        });
    }

    @Override
    public void onSlashCommand(Server server, SlashCommandEvent event) {
        TextChannel channel = event.getTextChannel();
        Member selfMember = event.getGuild().getSelfMember();
        Member sender = event.getMember();
        System.out.println("1");
        if (sender == null || GiveawayBot.isLocked() || !selfMember.hasPermission(channel, Permission.MESSAGE_WRITE)) {
            return;
        }
        System.out.println("2");
        if (!this.handleBotPermsCheck(server, channel, selfMember)) {
            return;
        }
        System.out.println("3");
        if (server == null) {
            this.languageRegistry.fallback(Text.FATAL_ERROR_LOADING_SERVER).to(event);
            return;
        }
        System.out.println("4");
        CompletableFuture.runAsync(() -> {
            long commandId = event.getCommandIdLong();
            SimpleCommand command = this.slashCommands.get(commandId);
            if (command == null) {
                JdaBot.LOGGER.error("Command not found with ID {} and path {}", commandId, event.getCommandPath());
                return;
            }
            if (!this.memberHasAccess(server, command, event, sender)) {
                return;
            }
            if (!server.isPremium() && command.requiresPremium()) {
                this.languageRegistry.get(server, Text.COMMAND_REQUIRES_PREMIUM).to(event);
                return;
            }
            String subCommandName = event.getSubcommandName();
            if (subCommandName == null) {
                this.executeCommand(command, sender, server, event);
                return;
            }
            SubCommand subCommand = command.getSubCommands().get(subCommandName);
            if (subCommand == null) {
                JdaBot.LOGGER.error("SubCommand not found with ID {} and path {}", event.getSubcommandName(), event.getCommandPath());
                return;
            }
            if (!server.isPremium() && subCommand.requiresPremium()) {
                this.languageRegistry.get(server, Text.COMMAND_REQUIRES_PREMIUM).to(event);
                return;
            }
            if (this.memberHasAccess(server, subCommand, event, sender)) {
                this.executeCommand(subCommand, sender, server, event);
            }
        }, this.executor).exceptionally(ex -> {
            JdaBot.LOGGER.error("Error from CommandBase input {}", event.getCommandPath(), ex);
            return null;
        });
    }

    /**
     * @return whether the bot has perms and can proceed
     */
    private boolean handleBotPermsCheck(Server server, TextChannel channel, Member selfMember) {
        if (!selfMember.hasPermission(channel, this.requiredPermissions)) {
            UserUtils.sendMissingPermsMessage(this.languageRegistry, server, selfMember, channel, channel);
            return false;
        }
        return true;
    }

    private void executeCommand(Command command, Member sender, Server server, SlashCommandEvent event) {
        this.executions.getAndIncrement();
        command.middleMan(sender, server, event);
    }

    private boolean memberHasAccess(Server server, Command simpleCommand, SlashCommandEvent event, Member member) {
        if (simpleCommand.requiresManager() && !server.canMemberManage(member)) {
            this.languageRegistry.get(server, Text.NO_PERMISSION).to(event);
            return false;
        }
        return true;
    }

    public Map<String, SimpleCommand> getCommands() {
        return this.commands;
    }

    public int retrieveExecutions() {
        return this.executions.getAndUpdate(previous -> 0);
    }
}