package pink.zak.giveawaybot.commands.preset.subs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.enums.Setting;
import pink.zak.giveawaybot.models.Preset;
import pink.zak.giveawaybot.service.command.command.SubCommand;

import java.util.List;

public class SetOptionSub extends SubCommand {
    private final ServerCache serverCache;

    public SetOptionSub(GiveawayBot bot) {
        super(bot);
        this.serverCache = bot.getServerCache();

        this.addFlat("set");
        this.addArgument(String.class); // preset name
        this.addArgument(String.class);  // Setting name
        this.addArgument(String.class); // Value
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
            String presetName = this.parseArgument(args, event.getGuild(), 1);
            if (presetName.equalsIgnoreCase("default")) {
                event.getChannel().sendMessage(":x: You cannot modify the default preset. Create your own using `>preset create <preset>` or `>preset setup`.").queue();
                return;
            }
            Preset preset = server.getPreset(presetName);
            if (preset == null) {
                event.getChannel().sendMessage(":x: Could not find a preset called ".concat(presetName)).queue();
                return;
            }
            String settingName = this.parseArgument(args, event.getGuild(), 2);
            Setting setting = Setting.match(settingName);
            if (setting == null) {
                event.getChannel().sendMessage(":x: Could not find a setting called " + settingName + ". Use `>preset settings` to list settings.").queue();
                return;
            }
            String inputValue = this.parseArgument(args, event.getGuild(), 3);
            if (!setting.checkInput(inputValue)) {
                event.getChannel().sendMessage(":x: Incorrect input for setting ".concat(settingName)).queue();
                return;
            }
            Object parsedValue = setting.parse(inputValue);
            if (!setting.checkLimit(parsedValue)) {
                event.getChannel().sendMessage(":x:".concat(setting.getLimitMessage())).queue();
                return;
            }
            preset.setSetting(setting, setting.parse(inputValue));
            event.getChannel().sendMessage("Set the " + setting.getPrimaryConfigName() + " setting to " + inputValue + ".").queue();
        });
    }
}
