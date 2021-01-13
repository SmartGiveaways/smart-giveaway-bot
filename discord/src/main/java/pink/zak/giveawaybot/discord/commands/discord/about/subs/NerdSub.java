package pink.zak.giveawaybot.discord.commands.discord.about.subs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.metrics.helpers.GenericBotMetrics;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.service.cache.singular.CachedValue;
import pink.zak.giveawaybot.discord.service.colour.Palette;
import pink.zak.giveawaybot.discord.service.time.Time;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class NerdSub extends SubCommand {
    private final GenericBotMetrics genericMetrics;
    private final GiveawayCache giveawayCache;
    private final Palette palette;
    private final CachedValue<Long> historicalGiveaways;

    public NerdSub(GiveawayBot bot) {
        super(bot, false, false, false);
        this.addFlatWithAliases("nerd", "nerds");

        this.genericMetrics = bot.getMetricsLogger().getGenericBotMetrics();
        this.giveawayCache = bot.getGiveawayCache();
        this.palette = bot.getDefaults().getPalette();
        this.historicalGiveaways = new CachedValue<>(TimeUnit.MINUTES, 30, () -> bot.getMongoConnectionFactory().getCollection("finished-giveaways").countDocuments());
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        event.getChannel().sendMessage(new EmbedBuilder()
                .setColor(this.palette.primary())
                .addField("__Running Information__", "**Uptime:** ".concat(Time.format(this.genericMetrics.getUptime())), true)
                .addField("", "**Servers:** ".concat(String.valueOf(this.genericMetrics.getGuilds())), true)
                .addField("", "**Giveaways:** ".concat(String.valueOf(this.giveawayCache.size())), true)
                .addBlankField(false)
                .addField("__Historical Information__", "**Giveaways:** ".concat(String.valueOf((this.historicalGiveaways.get() + this.giveawayCache.size()))), true)
                .addField("", "**Commands Ran:** ".concat("fuck"), true)
                .addBlankField(false)
                .addField("__Code Information__", "**Java:** 15 Preview", true)
                .addField("", "**JDA:** 4.2.0_227", true)
                .addField("", "**Guava:** 30.1", true)
                .addField("", "**YAML Wrapper:** 1.2.0", true)
                .addField("", "**Emojis:** 5.1.1", true)
                .addField("", "**Mongo Driver:** 3.12.7", true)
                .addField("", "**Influx Metrics:** 1.0", true)
                .addField("", "**InfluxDB Client:** 1.14", true)
                .addBlankField(false)
                .addField("Links", "[GitHub](https://github.com/SmartGiveaways/smart-giveaway-bot)", true)
                .addField("", "[Discord](https://discord.gg/aS4PebKZpe)", true)
                .addField("", "[Code Reports](https://app.codacy.com/gh/SmartGiveaways/smart-giveaway-bot/dashboard)", true)
                .build()).queue();
    }
}
