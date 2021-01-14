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
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;
import pink.zak.giveawaybot.discord.service.time.Time;

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
                .addField("", "**Commands Ran:** ".concat("fuck"), true) // TODO add the commands counter
                .addBlankField(false)
                .addField("Links", "[GitHub](https://github.com/SmartGiveaways/smart-giveaway-bot)", true)
                .addField("", "[Discord](https://discord.gg/aS4PebKZpe)", true)
                .addField("", "[Code Reports](https://app.codacy.com/gh/SmartGiveaways/smart-giveaway-bot/dashboard)", true)
                .build()).queue();
    }
}
