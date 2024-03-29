package pink.zak.giveawaybot.service.command.discord;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.utils.data.DataObject;
import pink.zak.giveawaybot.service.bot.JdaBot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

@UtilityClass
public class SlashCommandFileHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Set<SlashCommandInfo> loadSlashCommands(Path basePath) {
        Path path = basePath.resolve("command-data.json");
        FileReader reader;
        try {
            reader = new FileReader(path.toFile());
        } catch (FileNotFoundException ex) {
            return null;
        }
        JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

        Set<SlashCommandInfo> commands = Sets.newHashSet();
        for (JsonElement element : jsonObject.get("").getAsJsonArray()) {
            DataObject dataObject = DataObject.fromJson(element.toString());
            commands.add(new SlashCommandInfo(dataObject));
        }
        return commands;
    }

    public static void saveSlashCommands(Path basePath, Collection<Command> commands) {
        Path path = basePath.resolve("command-data.json");
        if (!Files.exists(path)) {
            try {
                boolean created = path.toFile().createNewFile();
                if (!created)
                    JdaBot.LOGGER.warn("Could not create command-data.json because the file already exists ???");
            } catch (IOException ex) {
                JdaBot.LOGGER.error("Error creating slash command file", ex);
            }
        }

        try (Writer writer = Files.newBufferedWriter(path)) {
            JsonObject json = new JsonObject();

            JsonArray jsonArray = new JsonArray();
            for (Command command : commands)
                jsonArray.add(createCommandObject(command));
            json.add("", jsonArray);
            GSON.toJson(json, writer);
        } catch (IOException ex) {
            JdaBot.LOGGER.error("Error writing slash commands", ex);
        }
    }

    // yes, more data can be saved into this but I can't be bothered as it isn't necessary. Most of the stuff here isn't necessary.
    private static JsonObject createCommandObject(Command command) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", command.getName());
        jsonObject.addProperty("description", command.getDescription());
        jsonObject.addProperty("id", command.getIdLong());
        jsonObject.addProperty("default_permission", command.isDefaultEnabled());
        jsonObject.addProperty("application_id", command.getApplicationIdLong());

        if (!command.getOptions().isEmpty()) {
            JsonArray options = new JsonArray();
            for (Command.Option option : command.getOptions()) {
                options.add(createOptionsObject(option));
            }
            jsonObject.add("options", options);
        }

        return jsonObject;
    }

    private static JsonObject createOptionsObject(Command.Option option) {
        JsonObject optionObject = new JsonObject();
        optionObject.addProperty("name", option.getName());
        optionObject.addProperty("description", option.getDescription());
        optionObject.addProperty("type", option.getTypeRaw());
        optionObject.addProperty("required", option.isRequired());

        if (!option.getChoices().isEmpty()) {
            JsonArray choices = new JsonArray();
            for (Command.Choice choice : option.getChoices()) {
                choices.add(createChoiceObject(choice));
            }
        }

        return optionObject;
    }

    private static JsonObject createChoiceObject(Command.Choice choice) {
        JsonObject choiceObject = new JsonObject();
        choiceObject.addProperty("name", choice.getName());
        choiceObject.addProperty("value", choice.getAsLong());
        return choiceObject;
    }

    public static class SlashCommandInfo {
        private final DataObject dataObject;


        public SlashCommandInfo(DataObject dataObject) {
            this.dataObject = dataObject;
        }

        public String getName() {
            return this.dataObject.getString("name");
        }

        public long getId() {
            return this.dataObject.getUnsignedLong("id");
        }

        public DataObject getDataObject() {
            return this.dataObject;
        }
    }
}
