package pink.zak.giveawaybot.api.controllers;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.RedirectView;
import pink.zak.giveawaybot.api.model.auth.AuthResponse;
import pink.zak.giveawaybot.api.model.generic.StringPayload;

@Component
public class BaseControllerImpl implements BaseController {

    @Override
    public RedirectView sendHomeRedirect() {
        return new RedirectView("https://smartgiveaways.xyz/");
    }

    @Override
    public AuthResponse auth(StringPayload payload) {
        String token = payload.getValue();
        return null;
    }
}
