package pink.zak.giveawaybot.commands.preset;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.preset.subs.*;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PresetCommand extends SimpleCommand {
    private final Palette palette;

    public PresetCommand(GiveawayBot bot) {
        super(bot, "preset");
        SetupSub setupSub = new SetupSub(bot);
        this.palette = bot.getDefaults().getPalette();

        this.setAliases("presets");
        this.setSubCommands(
                new CreateSub(bot),
                new DeleteSub(bot),
                new ListSub(bot),
                new OptionsSub(bot),
                new PresetOptionsSub(bot),
                new SetOptionSub(bot),
                setupSub
        );
        bot.registerListeners(setupSub);
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle("Preset Help")
                .setColor(this.palette.primary())
                .addField("Commands",
                        ">preset create <name>\n" +
                                ">preset settings <preset>\n" +
                                ">preset set <preset> <settings> <value>", false)
                .build()).queue(message -> {
            message.delete().queueAfter(60, TimeUnit.SECONDS, unused -> {
            }, this.bot.getDeleteFailureThrowable());
        });
    }
}
