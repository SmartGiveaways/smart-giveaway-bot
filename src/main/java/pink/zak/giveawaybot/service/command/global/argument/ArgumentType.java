package pink.zak.giveawaybot.service.command.global.argument;

import net.dv8tion.jda.api.entities.Guild;

public interface ArgumentType<T> {

    T parse(String arg, Guild guild);
}