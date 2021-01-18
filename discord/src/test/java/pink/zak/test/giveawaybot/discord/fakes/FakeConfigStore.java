package pink.zak.test.giveawaybot.discord.fakes;

import lombok.SneakyThrows;
import pink.zak.giveawaybot.discord.service.config.Config;
import pink.zak.giveawaybot.discord.service.config.ConfigStore;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class FakeConfigStore extends ConfigStore {

    public FakeConfigStore(Path basePath) {
        super(basePath);
    }

    @SneakyThrows
    @Override
    public ConfigStore config(String name, BiFunction<Path, String, Path> pathFunc, boolean reloadable) {
        this.configMap.put(name, new FakeConfig(path -> Paths.get(pathFunc.apply(path, name).toString().concat(".yml")), reloadable));
        return this;
    }
}
