package pink.zak.giveawaybot.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pink.zak.giveawaybot.api.model.server.PremiumTimeAdd;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.models.User;
import pink.zak.giveawaybot.discord.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.models.giveaway.FinishedGiveaway;
import pink.zak.giveawaybot.discord.models.giveaway.ScheduledGiveaway;

import java.util.List;

@RestController
@RequestMapping("/server/{serverId}")
public interface ServerController {

    @Operation(summary = "Get a Server")
    @GetMapping("")
    Server getServer(@PathVariable long serverId);

    @Operation(summary = "Get the current giveaways for a server")
    @GetMapping("/currentGiveaways")
    List<CurrentGiveaway> getCurrentGiveaways(@PathVariable long serverId);

    @Operation(summary = "Get the scheduled giveaways for a server")
    @GetMapping("/scheduledGiveaways")
    List<ScheduledGiveaway> getScheduledGiveaways(@PathVariable long serverId);

    @Operation(summary = "Get the finished giveaways for a server")
    @GetMapping("/finishedGiveaways")
    List<FinishedGiveaway> getFinishedGiveaways(@PathVariable long serverId);

    @Operation(summary = "Get the banned users for a server")
    @GetMapping("/bannedUsers")
    List<User> getBannedUsers(@PathVariable long serverId);

    @Operation(summary = "Add premium time to a server")
    @PatchMapping(value = "/addPremiumTime", consumes = MediaType.APPLICATION_JSON_VALUE)
    long addPremiumTime(@PathVariable long serverId, @RequestBody PremiumTimeAdd payload);
}
