package pink.zak.giveawaybot.discord.commands.discord.about.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.data.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.lang.Text;
import pink.zak.giveawaybot.discord.metrics.helpers.GenericMetrics;
import pink.zak.giveawaybot.discord.metrics.helpers.LatencyMonitor;
import pink.zak.giveawaybot.discord.data.models.Server;
import pink.zak.giveawaybot.discord.service.cache.singular.CachedValue;
import pink.zak.giveawaybot.discord.service.colour.Palette;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;
import pink.zak.giveawaybot.discord.service.time.Time;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class NerdSub extends SubCommand {
    private final GenericMetrics genericMetrics;
    private final LatencyMonitor latencyMonitor;
    private final GiveawayCache giveawayCache;
    private final Palette palette;
    private final CachedValue<Long> historicalGiveaways;

    public NerdSub(GiveawayBot bot) {
        super(bot, false, false, false);
        this.addFlatWithAliases("nerd", "nerds");

        this.genericMetrics = bot.getMetricsLogger().getGenericMetrics();
        this.latencyMonitor = bot.getLatencyMonitor();
        this.giveawayCache = bot.getGiveawayCache();
        this.palette = bot.getDefaults().getPalette();
        this.historicalGiveaways = new CachedValue<>(TimeUnit.MINUTES, 30, () -> bot.getMongoConnectionFactory().getCollection("finished-giveaways").countDocuments());
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        JDA jda = event.getJDA();
        event.getChannel().sendMessage(new EmbedBuilder()
                .setColor(this.palette.primary())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).get())
                .addField("**Uptime**", Time.format(this.genericMetrics.getUptime()), true)
                .addField("**Servers**", String.valueOf(this.genericMetrics.getGuilds()), true)
                .addField("**Users**", String.valueOf(this.genericMetrics.getUsers()), true)
                .addField("**Current Giveaways**", String.valueOf(this.giveawayCache.size()), true)
                .addField("**Finished Giveaways**", String.valueOf(this.historicalGiveaways.get()), true)
                .addField("**Shard**", jda.getShardInfo().getShardId() + "/" + (jda.getShardManager().getShardsTotal() - 1), true)
                .addField("**Shard Ping**", this.latencyMonitor.getLastTiming(jda) + "ms", true)
                .addField("**Average Shard Ping**", this.latencyMonitor.getAverageLatency() + "ms", true)
                .addBlankField(false)
                .addField("", "[GitHub](https://github.com/SmartGiveaways/smart-giveaway-bot)", true)
                .addField("", "[Discord](https://discord.gg/aS4PebKZpe)", true)
                .addField("", "[Code Reports](https://app.codacy.com/gh/SmartGiveaways/smart-giveaway-bot/dashboard)", true)
                .build()).queue();
    }
}
