package pink.zak.giveawaybot.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pink.zak.giveawaybot.discord.models.User;

@RestController
@RequestMapping("/server/{serverId}/{userId}")
public interface UserController {

    @Operation(summary = "Get a User")
    @GetMapping("/")
    User getUser(@PathVariable long serverId, @PathVariable long userId);

    @Operation(summary = "Get if a User can manage")
    @GetMapping("/isManager")
    boolean isUserManager(@PathVariable long serverId, @PathVariable long userId);
}
