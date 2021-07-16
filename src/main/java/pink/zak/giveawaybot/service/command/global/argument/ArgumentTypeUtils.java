package pink.zak.giveawaybot.service.command.global.argument;

import com.google.common.collect.Maps;

import java.util.Map;

public class ArgumentTypeUtils {
    private static final Map<Class<?>, ArgumentType<?>> argumentTypes = Maps.newHashMap();

    private ArgumentTypeUtils() {
        throw new IllegalStateException("Registry class cannot be instantiated.");
    }

    public static void register(Class<?> clazz, ArgumentType<?> argumentType) {
        argumentTypes.put(clazz, argumentType);
    }

    @SuppressWarnings("unchecked")
    public static <T> ArgumentType<T> getArgumentType(Class<?> clazz) {
        return (ArgumentType<T>) argumentTypes.get(clazz);
    }
}