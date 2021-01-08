package pink.zak.giveawaybot.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import pink.zak.giveawaybot.api.model.auth.AuthResponse;
import pink.zak.giveawaybot.api.model.generic.StringPayload;

@RestController
@RequestMapping("/")
public interface BaseController {

    @GetMapping("")
    RedirectView sendHomeRedirect();

    @GetMapping("auth")
    AuthResponse auth(@RequestBody StringPayload payload);
}
