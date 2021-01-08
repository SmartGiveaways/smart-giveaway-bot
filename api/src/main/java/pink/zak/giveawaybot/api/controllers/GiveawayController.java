package pink.zak.giveawaybot.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pink.zak.giveawaybot.api.model.giveaway.ScheduledGiveawayCreation;
import pink.zak.giveawaybot.discord.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.discord.models.giveaway.ScheduledGiveaway;

import java.util.UUID;

@RestController
@RequestMapping("/giveaway")
public interface GiveawayController {

    @Operation(summary = "Get a Scheduled Giveaway")
    @GetMapping("/scheduled/{uuid}")
    ScheduledGiveaway getScheduledGiveaway(@PathVariable UUID uuid);

    @Operation(summary = "Create a Scheduled Giveaway")
    @PostMapping("/scheduled/create/")
    ScheduledGiveaway createScheduledGiveaway(@RequestBody ScheduledGiveawayCreation payload);

    @Operation(summary = "Get a Current Giveaway")
    @GetMapping("/current/{id}")
    CurrentGiveaway getCurrentGiveaway(@PathVariable long id);

    @Operation(summary = "Get a Finished Giveaway")
    @GetMapping("/finished/{id}")
    FinishedGiveaway getFinishedGiveaway(@PathVariable long id);
}
