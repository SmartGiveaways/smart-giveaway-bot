package pink.zak.giveawaybot.service.storage;


import lombok.experimental.UtilityClass;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.service.storage.storage.Storage;

import java.util.function.Function;

@UtilityClass
public class StorageProvider {

    public static <T> Storage<T> provide(GiveawayBot bot, Function<BackendFactory, Backend> backend, Function<Backend, Storage<T>> instance) {
        return instance.apply(backend.apply(new BackendFactory(bot)));
    }

    public static <T> Storage<T> provide(BackendFactory backendFactory, Function<BackendFactory, Storage<T>> instance) {
        return instance.apply(backendFactory);
    }
}
