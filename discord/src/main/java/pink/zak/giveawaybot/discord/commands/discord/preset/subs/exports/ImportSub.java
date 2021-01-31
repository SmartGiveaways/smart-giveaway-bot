package pink.zak.giveawaybot.discord.commands.discord.preset.subs.exports;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.commands.discord.preset.subs.exports.utils.ImportCmdUtils;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;

import java.util.List;

public class ImportSub extends SubCommand {
    private final ImportCmdUtils cmdUtils;

    public ImportSub(GiveawayBot bot, ImportCmdUtils cmdUtils) {
        super(bot, true, true, false);
        this.addFlat("import");

        this.cmdUtils = cmdUtils;
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        this.cmdUtils.requestImport(server, event.getMessage());
    }
}
