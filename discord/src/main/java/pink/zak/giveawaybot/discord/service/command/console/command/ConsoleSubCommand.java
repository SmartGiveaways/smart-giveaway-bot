package pink.zak.giveawaybot.discord.service.command.console.command;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.cache.ServerCache;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.command.global.argument.Argument;
import pink.zak.giveawaybot.discord.service.command.global.argument.ArgumentTypeUtils;

import java.util.List;
import java.util.function.Predicate;

public abstract class ConsoleSubCommand extends ConsoleCommand {
    private final boolean endless;
    private final ServerCache serverCache;
    private List<Argument<?>> arguments = Lists.newArrayList();

    protected ConsoleSubCommand(@Nullable GiveawayBot bot, ServerCache serverCache, boolean endless) {
        super(bot);
        this.serverCache = serverCache;
        this.endless = endless;
    }

    public boolean isEndless() {
        return this.endless;
    }

    public void addFlat(String flat) {
        this.arguments.add(new Argument<>(null, flat));
    }

    public void addFlatWithAliases(String flat, String... aliases) {
        this.arguments.add(new Argument<>(null, flat, aliases));
    }

    public void addFlats(String... flat) {
        for (String flatArgument : flat) {
            this.addFlat(flatArgument);
        }
    }

    protected <S> void addArgument(Class<S> clazz) {
        this.arguments.add(new Argument<>(ArgumentTypeUtils.getArgumentType(clazz)));
    }

    protected <S> void addArgument(Class<S> clazz, Predicate<String> tester) {
        this.arguments.add(new Argument<>(ArgumentTypeUtils.getArgumentType(clazz), tester));
    }

    protected <S> void addArgument(Class<S> clazz, String argument) {
        this.arguments.add(new Argument<>(ArgumentTypeUtils.getArgumentType(clazz), argument));
    }

    protected void addArguments(Class<?>... clazzes) {
        for (Class<?> clazz : clazzes) {
            this.addArgument(clazz);
        }
    }

    public int getArgumentsSize() {
        return this.arguments.size();
    }

    @SuppressWarnings("unchecked")
    public <U> U parseArgument(List<String> args, int index) {
        return ((Argument<U>) this.arguments.get(index)).getType().parse(args.get(index), null);
    }

    public boolean isMatch(List<String> arguments) {
        return this.isMatch(arguments, arguments.size());
    }

    public boolean isEndlessMatch(List<String> arguments) {
        return this.isMatch(arguments, this.arguments.size());
    }

    public boolean isMatch(List<String> arguments, int endIndex) {
        for (int i = 0; i < endIndex; i++) {
            if (!this.isArgumentValid(arguments, i)) {
                return false;
            }
        }
        return true;
    }

    public String[] getEnd(List<String> arguments) {
        List<String> newList = Lists.newArrayList();
        for (int i = 0; i < arguments.size(); i++) {
            if (i < this.arguments.size() - 1) {
                continue;
            }
            newList.add(arguments.get(i));
        }
        return newList.toArray(new String[]{});
    }

    private boolean isArgumentValid(List<String> arguments, int index) {
        if (this.arguments.size() < index && this.endless) {
            return true;
        }
        Argument<?> argument = this.arguments.get(index);
        String matchTo = arguments.get(index);
        if (argument.getType() == null) {
            for (String alias : argument.getAliases()) {
                if (matchTo.equalsIgnoreCase(alias)) {
                    return true;
                }
            }
            return argument.getArgumentName() != null && arguments.get(index).equalsIgnoreCase(argument.getArgumentName());
        } else return argument.getTester() == null || argument.getTester().test(matchTo);
    }

    protected Server parseServerInput(List<String> args, int index) {
        long serverId = this.parseArgument(args, index);
        if (serverId == -1) {
            JdaBot.logger.error("Input is not a long ({})", args.get(1));
            return null;
        }
        Server server = this.serverCache.get(serverId);
        if (server == null) {
            JdaBot.logger.error("Could not find a server with the ID {}", serverId);
        }
        return server;
    }
}
