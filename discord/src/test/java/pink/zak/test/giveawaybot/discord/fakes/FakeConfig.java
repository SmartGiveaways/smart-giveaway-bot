package pink.zak.test.giveawaybot.discord.fakes;

import lombok.SneakyThrows;
import pink.zak.giveawaybot.discord.service.config.Config;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.function.UnaryOperator;

public class FakeConfig extends Config {

    public FakeConfig(File file, boolean reloadable) {
        super(file, reloadable);
    }

    public FakeConfig(UnaryOperator<Path> path, boolean reloadable) throws URISyntaxException {
        super(new File(ClassLoader.getSystemResource(path.apply(new File("").toPath()).toString()).toURI()), reloadable);
    }
}
