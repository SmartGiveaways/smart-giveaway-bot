package pink.zak.giveawaybot.commands.giveaway;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.commands.giveaway.subs.CreateSub;
import pink.zak.giveawaybot.commands.giveaway.subs.CreateWithChannelSub;
import pink.zak.giveawaybot.commands.giveaway.subs.RerollSub;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;
import pink.zak.giveawaybot.threads.ThreadManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GiveawayCommand extends SimpleCommand {
    private final ThreadManager threadManager;
    private final Palette palette;
    private MessageEmbed limitedMessageEmbed;
    private MessageEmbed fullMessageEmbed;

    public GiveawayCommand(GiveawayBot bot) {
        super(bot, false, "giveaway");
        GiveawayCmdUtils cmdUtils = new GiveawayCmdUtils(bot);
        this.setAliases("g", "ghelp");


        this.setSubCommands(
                new CreateSub(bot, cmdUtils),
                new CreateWithChannelSub(bot, cmdUtils),
                new RerollSub(bot)
        );

        this.palette = bot.getDefaults().getPalette();
        this.threadManager = bot.getThreadManager();
        this.buildMessages();
    }

    @Override
    public void onExecute(Member sender, Server server, MessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(server.canMemberManage(sender) ? this.fullMessageEmbed : this.limitedMessageEmbed).queue(embed -> {
            embed.delete().queueAfter(60, TimeUnit.SECONDS, null, this.bot.getDeleteFailureThrowable(), this.threadManager.getUpdaterExecutor());
            event.getMessage().delete().queueAfter(60, TimeUnit.SECONDS, null, this.bot.getDeleteFailureThrowable(), this.threadManager.getUpdaterExecutor());
        });
    }

    private void buildMessages() {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Smart Giveaways Help")
                .setColor(this.palette.primary())
                .addField("General Commands", ">entries", false);
        this.limitedMessageEmbed = embedBuilder.build();

        embedBuilder.addField("Admin Commands",
                """
                        >giveaway create <preset> <length> <#channel> <no. winners> <topic>
                                                
                        >preset list - Lists all your presets
                        >preset create <name> - Creates a new preset for giveaways
                        >preset settings <preset> - Shows the set values for a preset
                        >preset set <preset> <setting> <value> - Sets a value for a preset
                                                
                        >gban <user> - Visibly bans a user from giveaways.
                        >gsban <user> - Shadow bans a user from giveaways. Almost impossible to tell.
                        >gunban <user> - Removes a user's ban or shadow ban.
                        """, false);
        this.fullMessageEmbed = embedBuilder.build();
    }
}
