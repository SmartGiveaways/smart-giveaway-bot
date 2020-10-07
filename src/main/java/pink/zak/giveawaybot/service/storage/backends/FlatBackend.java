package pink.zak.giveawaybot.service.storage.backends;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import pink.zak.giveawaybot.service.storage.Backend;

import javax.naming.OperationNotSupportedException;
import java.io.FileReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlatBackend implements Backend {
    private final Path path;

    public FlatBackend(Path subPath) {
        this.path = this.createPath(subPath);
    }

    @Override
    @SneakyThrows
    public JsonObject load(String id) {
        Path userPath = this.path.resolve(id + ".json");
        if (!Files.exists(userPath)) {
            return null;
        }
        FileReader reader = new FileReader(userPath.toFile());
        return JsonParser.parseReader(reader).getAsJsonObject();
    }

    @SneakyThrows
    @Override
    public JsonObject load(Map<String, String> valuePairs) {
        throw new OperationNotSupportedException("Method not available with flat file storage");
    }

    @Override
    @SneakyThrows
    public void save(String id, JsonObject json) {
        if (Files.exists(this.path)) {
            Path userPath = this.path.resolve(id + ".json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Writer writer = Files.newBufferedWriter(userPath);
            gson.toJson(json, writer);
            writer.close();
        }
    }

    @SneakyThrows
    @Override
    public void save(Map<String, String> valuePairs, JsonObject json) {
        throw new OperationNotSupportedException("Method not available with flat file storage");
    }

    @Override
    @SneakyThrows
    public Set<JsonObject> loadAll() {
        try (Stream<Path> stream = Files.walk(this.path)) {
            return stream
                    .map(Path::toString)
                    .filter(file -> file.endsWith(".json"))
                    .map(file -> file.replace(".json", ""))
                    .map(this::load)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

        }
    }

    @SneakyThrows
    @Override
    public void delete(String id) {
        Path filePath = this.path.resolve(id + ".json");
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Flat file JSON does not have a backend database to close.");
    }

    @SneakyThrows
    private Path createPath(Path path) {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return path;
        }
        path.toFile().mkdirs();
        return path;
    }
}
