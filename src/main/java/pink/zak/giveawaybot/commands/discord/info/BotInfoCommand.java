package pink.zak.giveawaybot.commands.discord.info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.sharding.ShardManager;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.cache.GiveawayCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.metrics.MetricsLogger;
import pink.zak.giveawaybot.metrics.helpers.LatencyMonitor;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.BotCommand;
import pink.zak.giveawaybot.service.time.Time;

public class BotInfoCommand extends BotCommand {
    private final MetricsLogger metricsLogger;
    private final GiveawayCache giveawayCache;
    private final LatencyMonitor latencyMonitor;
    private final ShardManager shardManager;

    private final Palette palette;

    public BotInfoCommand(GiveawayBot bot) {
        super(bot, "botinfo", false, false);

        this.metricsLogger = bot.getMetricsLogger();
        this.giveawayCache = bot.getGiveawayCache();
        this.latencyMonitor = bot.getLatencyMonitor();
        this.shardManager = bot.getShardManager();

        this.palette = bot.getDefaults().getPalette();
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        JDA jda = event.getJDA();
        event.replyEmbeds(new EmbedBuilder()
                .setTitle(super.languageRegistry.get(server, Text.ABOUT_EMBED_TITLE).toString())
                .setFooter(super.languageRegistry.get(server, Text.GENERIC_EMBED_FOOTER).toString())
                .setColor(this.palette.primary())
                .setDescription(super.languageRegistry.get(server, Text.ABOUT_EMBED_CONTENT, replacer -> replacer
                        .set("servers", this.metricsLogger.getGuildCount())
                        .set("users", this.metricsLogger.getGenericMetrics().getUsers())
                        .set("active_giveaways", this.giveawayCache.size())
                        .set("shard", jda.getShardInfo().getShardId() + 1)
                        .set("total_shards", this.shardManager.getShardsTotal())
                        .set("shard_ping", this.latencyMonitor.getLastTiming(jda))
                        .set("last_shard_ping_update", Time.format(System.currentTimeMillis() - this.latencyMonitor.getShardTestTimes().get(jda))))
                        .toString())
                .build())
            .setEphemeral(true).queue();
    }

    @Override
    public CommandData createCommandData() {
        return new CommandData("botinfo", "Get basic bot information");
    }
}
