//package pink.zak.giveawaybot.commands.discord.preset.subs.exports;
//
//import com.google.gson.Gson;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.TextChannel;
//import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
//import org.json.JSONObject;
//import pink.zak.giveawaybot.GiveawayBot;
//import pink.zak.giveawaybot.data.models.Server;
//import pink.zak.giveawaybot.data.storage.ServerStorage;
//import pink.zak.giveawaybot.lang.Text;
//import pink.zak.giveawaybot.service.bot.JdaBot;
//import pink.zak.giveawaybot.service.command.discord.command.SubCommand;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//public class ExportAllSub extends SubCommand {
//    private final Gson gson = new Gson();
//    private final ServerStorage serverStorage;
//
//    public ExportAllSub(GiveawayBot bot) {
//        super(bot, true, true, false);
//        this.addFlat("export");
//        this.addFlat("all");
//
//        this.serverStorage = bot.getServerStorage();
//    }
//
//    @Override
//    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
//        this.exportAll(server, event.getChannel());
//    }
//
//    private void exportAll(Server server, TextChannel channel) {
//        String fileName = "presets-" + server.getId() + ".json";
//        String json = this.gson.toJson(this.serverStorage.serializePresets(server.getPresets()));
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("singular", false);
//        jsonObject.put("preset-values", json);
//
//        try (InputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes(StandardCharsets.UTF_8))) {
//            channel.sendMessage(this.langFor(server, Text.PRESET_EXPORTED_ALL).toString()).addFile(inputStream, fileName).queue(message -> {}, ex -> JdaBot.LOGGER.error("Error sending exported preset", ex));
//        } catch (IOException ex) {
//            JdaBot.LOGGER.error("Error exporting presets", ex);
//        }
//    }
//}
