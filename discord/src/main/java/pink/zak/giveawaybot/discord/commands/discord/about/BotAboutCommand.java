package pink.zak.giveawaybot.discord.commands.discord.about;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.commands.discord.about.subs.NerdSub;
import pink.zak.giveawaybot.discord.lang.LanguageRegistry;
import pink.zak.giveawaybot.discord.lang.enums.Text;
import pink.zak.giveawaybot.discord.metrics.MetricsLogger;
import pink.zak.giveawaybot.discord.metrics.helpers.LatencyMonitor;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.service.colour.Palette;
import pink.zak.giveawaybot.discord.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.discord.service.time.Time;

import java.util.List;

public class BotAboutCommand extends SimpleCommand {
    private final MetricsLogger metricsLogger;
    private final GiveawayCache giveawayCache;
    private final LatencyMonitor latencyMonitor;
    private final ShardManager shardManager;

    private final LanguageRegistry languageRegistry;
    private final Palette palette;

    public BotAboutCommand(GiveawayBot bot) {
        super(bot, "babout", false, false);
        this.setAliases("binfo", "gabout", "ginfo", "whatthisbotdo");
        this.setSubCommands(
                new NerdSub(bot)
        );

        this.metricsLogger = bot.getMetricsLogger();
        this.giveawayCache = bot.getGiveawayCache();
        this.latencyMonitor = bot.getLatencyMonitor();
        this.shardManager = bot.getShardManager();

        this.languageRegistry = bot.getLanguageRegistry();
        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        JDA jda = event.getJDA();
        event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle(this.languageRegistry.get(server, Text.ABOUT_EMBED_TITLE).get())
                .setFooter(this.languageRegistry.get(server, Text.GENERIC_EMBED_FOOTER).get())
                .setColor(this.palette.primary())
                .setDescription(this.languageRegistry.get(server, Text.ABOUT_EMBED_CONTENT, replacer -> replacer
                        .set("servers", this.metricsLogger.getGuildCount())
                        .set("users", this.metricsLogger.getGenericMetrics().getUsers())
                        .set("active_giveaways", this.giveawayCache.size())
                        .set("shard", jda.getShardInfo().getShardId())
                        .set("total_shards", this.shardManager.getShardsTotal() - 1)
                        .set("shard_ping", this.latencyMonitor.getLastTiming(jda))
                        .set("last_shard_ping_update", Time.format(System.currentTimeMillis() - this.latencyMonitor.getShardTestTimes().get(jda))))
                        .get())
                .build()).queue();
    }
}
