package pink.zak.giveawaybot.api.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public interface StatsController {

    @Operation(summary = "Get the bot uptime")
    @GetMapping("/uptime")
    String getUptime(@JsonProperty(value = "formatted", defaultValue = "false") Boolean formatted);
}
