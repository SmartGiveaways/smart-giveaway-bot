package pink.zak.giveawaybot.commands.giveaway;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.commands.giveaway.subs.CreateSub;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;
import pink.zak.giveawaybot.threads.ThreadManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GiveawayCommand extends SimpleCommand {
    private final ServerCache serverCache;
    private final ThreadManager threadManager;
    private final Palette palette;

    public GiveawayCommand(GiveawayBot bot) {
        super(bot, false, "giveaway");
        this.setAliases("g", "help");
        this.setSubCommands(
                new CreateSub(bot)
        );

        this.palette = bot.getDefaults().getPalette();
        this.serverCache = bot.getServerCache();
        this.threadManager = bot.getThreadManager();
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, List<String> args) {
        this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Smart Giveaways Help")
                    .setColor(this.palette.primary())
                    .addField("General Commands", ">entries", false);
            if (sender.hasPermission(Permission.ADMINISTRATOR) || server.canMemberManage(sender)) {
                embedBuilder.addField( "Admin Commands",
                        """
                                >giveaway create <preset> <length> <#channel> <no. winners> <topic>
                                >preset create <name> - Creates a new preset for giveaways
                                >preset settings <preset> - Shows the set values for a preset
                                >preset set <preset> <setting> <value> - Sets a value for a preset""", false);
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue(embed -> {
                embed.delete().queueAfter(60, TimeUnit.SECONDS, null, this.bot.getDeleteFailureThrowable(), this.threadManager.getUpdaterExecutor());
                event.getMessage().delete().queueAfter(60, TimeUnit.SECONDS, null, this.bot.getDeleteFailureThrowable(), this.threadManager.getUpdaterExecutor());
            });
        });
    }
}
