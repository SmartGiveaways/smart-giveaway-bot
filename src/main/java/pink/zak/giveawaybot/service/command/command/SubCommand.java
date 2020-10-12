package pink.zak.giveawaybot.service.command.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.command.argument.Argument;
import pink.zak.giveawaybot.service.command.argument.ArgumentHandler;

import java.util.List;
import java.util.Set;

public abstract class SubCommand extends Command {
    private final boolean endless;
    private List<Argument<?>> arguments = Lists.newArrayList();

    public SubCommand(GiveawayBot bot, boolean requiresManager, boolean endless) {
        super(bot, requiresManager);
        this.endless = endless;
    }

    public SubCommand(GiveawayBot bot, boolean requiresManager) {
        this(bot, requiresManager, false);
    }

    public SubCommand(GiveawayBot bot) {
        this(bot, false, false);
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
        this.arguments.add(new Argument<S>(ArgumentHandler.getArgumentType(clazz)));
    }

    protected <S> void addArgument(Class<S> clazz, String argument) {
        this.arguments.add(new Argument<S>(ArgumentHandler.getArgumentType(clazz), argument));
    }

    protected <S> void addArguments(Class<S>... clazzes) {
        for (Class<S> clazz : clazzes) {
            this.addArgument(clazz);
        }
    }

    public int getArgumentsSize() {
        return this.arguments.size();
    }

    @SuppressWarnings("unchecked")
    public <U> U parseArgument(List<String> args, Guild guild, int index) {
        return ((Argument<U>) this.arguments.get(index)).getType().parse(args.get(index), guild);
    }

    public boolean isMatch(List<String> arguments) {
        for (int i = 0; i < arguments.size(); i++) {
            if (!this.isArgumentValid(arguments, i)) {
                return false;
            }
        }
        return true;
    }

    public String[] getEnd(List<String> arguments) {
        Set<String> newSet = Sets.newLinkedHashSet();
        for (int i = 0; i < arguments.size(); i++) {
            if (i < this.arguments.size() - 1) {
                continue;
            }
            newSet.add(arguments.get(i));
        }
        return newSet.toArray(new String[]{});
    }

    private boolean isArgumentValid(List<String> arguments, int index) {
        if (this.arguments.size() < index && this.endless) {
            return true;
        }
        Argument<?> argument = this.arguments.get(index);
        if (argument.getType() == null) {
            String matchTo = arguments.get(index);
            for (String alias : argument.getAliases()) {
                if (matchTo.equalsIgnoreCase(alias)) {
                    return true;
                }
            }
            return argument.getArgument() != null && arguments.get(index).equalsIgnoreCase(argument.getArgument());
        }
        return true;
    }

    @Override
    public abstract void onExecute(Member sender, MessageReceivedEvent event, List<String> args);
}