package pink.zak.giveawaybot.discord.commands.discord.preset.subs.exports;

import com.google.gson.Gson;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONObject;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.models.Preset;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.lang.Text;
import pink.zak.giveawaybot.discord.service.bot.JdaBot;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExportSub extends SubCommand {
    private final Gson gson = new Gson();

    public ExportSub(GiveawayBot bot) {
        super(bot, true, true, false);
        this.addFlat("export");
        this.addArgument(Preset.class);
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        TextChannel channel = event.getChannel();
        Preset preset = this.parseArgument(args, event.getGuild(), 1);
        if (preset == null) {
            this.langFor(server, Text.COULDNT_FIND_PRESET).to(channel);
            return;
        }
        if (!event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_ATTACH_FILES)) {
            this.langFor(server, Text.BOT_MISSING_PERMISSION_SPECIFIC, replacer -> replacer.set("permission", "`MESSAGE_ATTACH_FILES`")).to(channel);
            return;
        }
        this.export(server, preset, channel);
    }

    private void export(Server server, Preset preset, TextChannel channel) {
        String fileName = "presets-" + preset.getName() + "-" + server.getId() + ".json";
        String json = this.gson.toJson(preset.getSerializedSettings());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("singular", true);
        jsonObject.put("preset-name", preset.getName());
        jsonObject.put("preset-values", json);

        try (InputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes(StandardCharsets.UTF_8))) {
            channel.sendMessage(this.langFor(server, Text.PRESET_EXPORTED_SINGLE, replacer -> replacer
                    .set("preset", preset.getName())).toString()).addFile(inputStream, fileName).queue(message -> {}, ex -> JdaBot.logger.error("Error sending exported preset", ex));
        } catch (IOException ex) {
            JdaBot.logger.error("Error exporting preset", ex);
        }
    }
}
