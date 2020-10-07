package pink.zak.giveawaybot.commands.giveaway;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.cache.ServerCache;
import pink.zak.giveawaybot.commands.giveaway.subs.CreateSub;
import pink.zak.giveawaybot.service.command.command.SimpleCommand;
import pink.zak.giveawaybot.threads.ThreadManager;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class GiveawayCommand extends SimpleCommand {
    private final ServerCache serverCache;
    private final ThreadManager threadManager;

    public GiveawayCommand(GiveawayBot bot) {
        super(bot, "giveaway");
        this.setAliases("help");
        this.setSubCommands(
                new CreateSub(bot)
        );

        this.serverCache = bot.getServerCache();
        this.threadManager = bot.getThreadManager();
    }

    @Override
    public void onExecute(Member sender, MessageReceivedEvent event, String[] args) {
        /*Map<Integer, AtomicInteger> amount = Maps.newHashMap();
        for (int i = 1; i < 100; i++) {
            BigInteger random = NumberUtils.getRandomBigInteger(new BigInteger("4"));
            if (amount.containsKey(random.intValue())) {
                amount.get(random.intValue()).getAndIncrement();
            } else {
                amount.put(random.intValue(), new AtomicInteger(1));
            }
        }
        System.out.println(amount);*/
        this.serverCache.get(event.getGuild().getIdLong()).thenAccept(server -> {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Smart Giveaways Help")
                    .setColor(Color.PINK)
                    .addField("General Commands", ">entries", false);
            if (sender.hasPermission(Permission.ADMINISTRATOR) || server.canMemberManage(sender)) {
                embedBuilder.addField( "Admin Commands",
                        ">giveaway create <preset> <length> <#channel> <no. winners> <item given away>\n" +
                                ">preset create <name>\n" +
                                ">preset settings <preset>\n" +
                                ">preset set <preset> <setting> <value>", false);
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue(embed -> {
                embed.delete().queueAfter(60, TimeUnit.SECONDS, null, this.bot.getDeleteFailureThrowable(), this.threadManager.getUpdaterExecutor());
                event.getMessage().delete().queueAfter(60, TimeUnit.SECONDS, null, this.bot.getDeleteFailureThrowable(), this.threadManager.getUpdaterExecutor());
            });
        });
    }
}
