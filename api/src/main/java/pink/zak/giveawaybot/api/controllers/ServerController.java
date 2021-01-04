package pink.zak.giveawaybot.api.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pink.zak.giveawaybot.models.Server;
import pink.zak.giveawaybot.models.User;

@RestController
@RequestMapping("/server")
@Api(value = "Server", tags = {"Server"})
public interface ServerController {

    @ApiOperation("Get a Server")
    @GetMapping("/{id}")
    Server getServer(@PathVariable long id);

    @ApiOperation("Get a User")
    @GetMapping("/{serverId}/{userId}")
    User getUser(@PathVariable long serverId, @PathVariable long userId);
}
