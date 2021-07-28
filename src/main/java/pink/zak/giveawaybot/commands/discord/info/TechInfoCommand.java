package pink.zak.giveawaybot.commands.discord.info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.cache.GiveawayCache;
import pink.zak.giveawaybot.data.models.Server;
import pink.zak.giveawaybot.lang.Text;
import pink.zak.giveawaybot.metrics.helpers.GenericMetrics;
import pink.zak.giveawaybot.metrics.helpers.LatencyMonitor;
import pink.zak.giveawaybot.service.cache.singular.CachedValue;
import pink.zak.giveawaybot.service.colour.Palette;
import pink.zak.giveawaybot.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.service.time.Time;

import java.util.concurrent.TimeUnit;

public class TechInfoCommand extends SimpleCommand {
    private final GenericMetrics genericMetrics;
    private final LatencyMonitor latencyMonitor;
    private final GiveawayCache giveawayCache;
    private final Palette palette;
    private final CachedValue<Long> historicalGiveaways;

    public TechInfoCommand(GiveawayBot bot) {
        super(bot, "techinfo", false, false);

        this.genericMetrics = bot.getMetricsLogger().getGenericMetrics();
        this.latencyMonitor = bot.getLatencyMonitor();
        this.giveawayCache = bot.getGiveawayCache();
        this.palette = bot.getDefaults().getPalette();
        this.historicalGiveaways = new CachedValue<>(TimeUnit.MINUTES, 30, () -> bot.getMongoConnectionFactory().getCollection("finished-giveaways").countDocuments());
    }

    @Override
    public void onExecute(Member sender, Server server, SlashCommandEvent event) {
        JDA jda = event.getJDA();
        event.replyEmbeds(new EmbedBuilder()
                .setColor(this.palette.primary())
                .setFooter(this.langFor(server, Text.GENERIC_EMBED_FOOTER).toString())
                .addField("**Uptime**", Time.format(this.genericMetrics.getUptime()), true)
                .addField("**Servers**", String.valueOf(this.genericMetrics.getGuilds()), true)
                .addField("**Users**", String.valueOf(this.genericMetrics.getUsers()), true)
                .addField("**Current Giveaways**", String.valueOf(this.giveawayCache.size()), true)
                .addField("**Finished Giveaways**", String.valueOf(this.historicalGiveaways.get()), true)
                .addField("**Shard**", jda.getShardInfo().getShardId() + 1 + "/" + (jda.getShardManager().getShardsTotal()), true)
                .addField("**Shard Ping**", this.latencyMonitor.getLastTiming(jda) + "ms", true)
                .addField("**Average Shard Ping**", this.latencyMonitor.getAverageLatency() + "ms", true)
                .addBlankField(false)
                .addField("", "[GitHub](https://github.com/SmartGiveaways/smart-giveaway-bot)", true)
                .addField("", "[Discord](https://discord.gg/aS4PebKZpe)", true)
                .addField("", "[Code Reports](https://sonarcloud.io/dashboard?id=SmartGiveaways_smart-giveaway-bot)", true)
                .build())
            .setEphemeral(true).queue();
    }

    @Override
    public CommandData createCommandData() {
        return new CommandData("techinfo", "Get nerdy information about the bot");
    }
}
