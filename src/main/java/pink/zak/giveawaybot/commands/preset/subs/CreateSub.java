package pink.zak.giveawaybot.commands.preset.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public class CreateSub extends SubCommand {
    private final ServerCache serverCache;

    public CreateSub(GiveawayBot bot) {
        super(bot);
        this.serverCache = bot.getServerCache();

        this.addFlat("create");
        this.addArgument(String.class);
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        String name = this.parseArgument(args, event.getGuild(), 1);
        this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
            if (server.getPreset(name) != null) {
                event.getChannel().sendMessage("There is already a preset called " + name + " for this server.").queue();
                return;
            }
            if (name.length() > 20) {
                event.getChannel().sendMessage("Preset names must be shorter than 20 characters.").queue();
                return;
            }
            if (name.length() < 4) {
                event.getChannel().sendMessage("Preset names must be longer than 3 characters.").queue();
                return;
            }
            event.getChannel().sendMessage("Created your preset. Use `>preset settings` to see available settings.").queue();
            server.addPreset(new Preset(name));
        });
    }
}
