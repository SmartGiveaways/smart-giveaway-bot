package pink.zak.giveawaybot.commands.preset.subs;

import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.lang.enums.Text;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.command.command.SubCommand;
import pink.zak.giveawaybot.storage.ServerStorage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExportSub extends SubCommand {
    private final Gson gson = new Gson();
    private final ServerStorage serverStorage;

    public ExportSub(GiveawayBot bot) {
        super(bot, true, true, false);
        this.addFlat("export");
        this.addArgument(Preset.class);

        this.serverStorage = bot.getServerStorage();
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        TextChannel channel = event.getChannel();
        if (args.get(1).equalsIgnoreCase("all")) {
            this.exportAll(server, channel);
            return;
        }
        Preset preset = this.parseArgument(args, event.getGuild(), 1);
        if (preset == null) {
            this.langFor(server, Text.COULDNT_FIND_PRESET).to(channel);
            return;
        }
        this.export(server, preset, channel);
    }

    private void exportAll(Server server, TextChannel channel) {
        String fileName = "presets-" + server.getId() + ".json";
        String json = this.gson.toJson(this.serverStorage.serializePresets(server.getPresets()));
        InputStream inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        try {
            channel.sendMessage(this.langFor(server, Text.PRESET_EXPORTED_ALL).get()).addFile(inputStream, fileName).queue(message -> {}, Throwable::printStackTrace);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void export(Server server, Preset preset, TextChannel channel) {
        //channel.sendFile(, this.langFor(server, Text.PRESET_EXPORTED_SINGLE, replacer -> replacer.set("preset", preset.name())).get()).queue();

    }
}
