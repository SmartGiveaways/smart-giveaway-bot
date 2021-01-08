package pink.zak.giveawaybot.api.controllers;

import org.springframework.stereotype.Component;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.service.time.Time;
import pink.zak.giveawaybot.discord.metrics.helpers.GenericBotMetrics;

@Component
public class StatsControllerImpl implements StatsController {
    private final GenericBotMetrics metrics = GiveawayBot.apiInstance.getMetricsLogger().getGenericBotMetrics();

    @Override
    public String getUptime(Boolean formatted) {
        long uptime = this.metrics.getUptime();
        if (formatted) {
            return Time.format(uptime);
        }
        return String.valueOf(uptime);
    }
}
