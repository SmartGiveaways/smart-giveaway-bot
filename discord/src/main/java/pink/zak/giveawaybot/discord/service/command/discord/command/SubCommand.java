package pink.zak.giveawaybot.discord.service.command.discord.command;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.service.command.global.argument.Argument;
import pink.zak.giveawaybot.discord.service.command.global.argument.ArgumentTypeUtils;

import java.util.List;
import java.util.function.Predicate;

public abstract class SubCommand extends Command {
    private final boolean endless;
    private List<Argument<?>> arguments = Lists.newArrayList();

    public SubCommand(GiveawayBot bot, boolean manager, boolean premium, boolean endless) {
        super(bot, manager, premium);
        this.endless = endless;
    }

    public boolean isEndless() {
        return this.endless;
    }

    public void setArguments(List<Argument<?>> arguments) {
        this.arguments = arguments;
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

    @SafeVarargs
    protected final <S> void addArguments(Class<S>... clazzes) {
        for (Class<S> clazz : clazzes) {
            this.addArgument(clazz);
        }
    }

    public int argsSize() {
        return this.arguments.size();
    }

    @SuppressWarnings("unchecked")
    public <U> U parseArgument(List<String> args, Guild guild, int index) {
        return ((Argument<U>) this.arguments.get(index)).getType().parse(args.get(index), guild);
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

    @Override
    public abstract void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args);
}