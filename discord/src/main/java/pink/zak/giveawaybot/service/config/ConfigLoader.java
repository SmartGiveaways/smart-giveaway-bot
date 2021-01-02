package pink.zak.giveawaybot.service.config;

import java.util.List;
import java.util.function.Consumer;

public class ConfigLoader {

    private ConfigLoader() {
        throw new IllegalStateException("Not Instantiable");
    }

    public static Reader reader(Config config) {
        return new Reader(config);
    }

    public static class Reader {
        private final Config config;
        private String currentPath = "";

        public Reader(Config config) {
            this.config = config;
        }

        public Reader readWrap(Consumer<Reader> reader) {
            reader.accept(this);
            return this;
        }

        public String getCurrentPath() {
            return this.currentPath;
        }

        public void setCurrentPath(String currentPath) {
            this.currentPath = currentPath;
        }

        public String string(String path) {
            return this.config.string(this.currentPath + "." + path);
        }

        public String string() {
            return this.config.string(this.currentPath);
        }

        public int integer(String path) {
            return this.config.integer(this.currentPath + "." + path);
        }

        public int integer() {
            return this.config.integer(this.currentPath);
        }

        public List<String> list(String path) {
            return this.config.list(this.currentPath + "." + path);
        }

        public List<String> list() {
            return this.config.list(this.currentPath);
        }

        public Reader keyLoop(String path, Consumer<String> consumer) {
            if (this.currentPath.isEmpty()) {
                this.currentPath = path;
            } else if (!path.isEmpty()) {
                this.currentPath += ".".concat(path);
            }
            for (String key : this.config.keys(path)) {
                this.currentPath = path.isEmpty() ? key : path + "." + key;
                consumer.accept(key);
            }
            return this;
        }

        public Reader keyLoop(Consumer<String> consumer) {
            return this.keyLoop("", consumer);
        }
    }
}
