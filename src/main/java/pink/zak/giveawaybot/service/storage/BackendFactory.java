package pink.zak.giveawaybot.service.storage;

import com.google.common.collect.Maps;
import pink.zak.giveawaybot.service.bot.SimpleBot;
import pink.zak.giveawaybot.service.storage.backends.FlatBackend;
import pink.zak.giveawaybot.service.storage.backends.mongodb.MongoBackend;
import pink.zak.giveawaybot.service.storage.backends.mysql.MySqlBackend;
import pink.zak.giveawaybot.service.storage.settings.StorageType;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class BackendFactory {
    private final SimpleBot bot;
    private final Map<StorageType, BiFunction<UnaryOperator<Path>, String, Backend>> backendMap = Collections.synchronizedMap(Maps.newEnumMap(StorageType.class));

    public BackendFactory(SimpleBot bot) {
        this.bot = bot;
        this.addBackend(StorageType.MYSQL, destination -> new MySqlBackend(this.bot, destination)).addBackend(StorageType.MONGODB, destination -> new MongoBackend(bot, destination));
    }

    public Backend create(String backendType, UnaryOperator<Path> path, String destination) {
        for (Map.Entry<StorageType, BiFunction<UnaryOperator<Path>, String, Backend>> backendEntry : this.backendMap.entrySet()) {
            if (backendEntry.getKey().toString().equalsIgnoreCase(backendType)) {
                return backendEntry.getValue().apply(path, destination);
            }
        }
        return new FlatBackend(path.apply(this.bot.getBasePath().toAbsolutePath()).resolve(destination));
    }

    public Backend create(StorageType storageType, UnaryOperator<Path> path, String destination) {
        if (!this.backendMap.containsKey(storageType)) {
            return new FlatBackend(path.apply(this.bot.getBasePath().toAbsolutePath()).resolve(destination));
        }
        return this.backendMap.get(storageType).apply(path, destination);
    }

    public Backend create(StorageType storageType, String destination) {
        return this.create(storageType, path -> path, destination);
    }

    public Backend create(StorageType storageType, UnaryOperator<Path> path) {
        return this.create(storageType, path, "");
    }

    public BackendFactory addBackend(StorageType storageType, BiFunction<UnaryOperator<Path>, String, Backend> backend) {
        this.backendMap.put(storageType, backend);
        return this;
    }

    public BackendFactory addBackend(StorageType storageType, Function<String, Backend> backend) {
        this.addBackend(storageType, (path, destination) -> backend.apply(destination));
        return this;
    }

    public BackendFactory addBackendAsPath(StorageType storageType, Function<UnaryOperator<Path>, Backend> backend) {
        this.addBackend(storageType, (path, destination) -> backend.apply(path));
        return this;
    }
}
