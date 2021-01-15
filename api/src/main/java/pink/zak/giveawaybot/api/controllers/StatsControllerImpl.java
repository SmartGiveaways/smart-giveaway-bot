package pink.zak.giveawaybot.api.controllers;

import org.springframework.stereotype.Component;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.metrics.helpers.GenericMetrics;
import pink.zak.giveawaybot.discord.service.time.Time;

@Component
public class StatsControllerImpl implements StatsController {
    private final GenericMetrics metrics = GiveawayBot.apiInstance.getMetricsLogger().getGenericMetrics();

    @Override
    public String getUptime(Boolean formatted) {
        long uptime = this.metrics.getUptime();
        if (formatted) {
            return Time.format(uptime);
        }
        return String.valueOf(uptime);
    }
}
