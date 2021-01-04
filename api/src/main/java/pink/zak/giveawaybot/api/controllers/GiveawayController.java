package pink.zak.giveawaybot.api.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import pink.zak.giveawaybot.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.models.giveaway.ScheduledGiveaway;

import java.util.UUID;

@RestController
@RequestMapping("/giveaway")
@Api(value = "Giveaway", tags = {"Giveaway"})
public interface GiveawayController {

    @ApiOperation("Get a Scheduled Giveaway")
    @GetMapping("/scheduled/{uuid}")
    ScheduledGiveaway getScheduledGiveaway(@PathVariable UUID uuid);

    @ApiOperation("Create a Scheduled Giveaway")
    @PostMapping("/scheduled/create/")
    ScheduledGiveaway createScheduledGiveaway();

    @ApiOperation("Get a Current Giveaway")
    @GetMapping("/current/{id}")
    CurrentGiveaway getCurrentGiveaway(@PathVariable long id);

    @ApiOperation("Get a Finished Giveaway")
    @GetMapping("/finished/{id}")
    FinishedGiveaway getFinishedGiveaway(@PathVariable long id);
}
