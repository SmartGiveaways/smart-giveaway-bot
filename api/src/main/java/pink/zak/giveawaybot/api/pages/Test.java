package pink.zak.giveawaybot.api.pages;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pink.zak.giveawaybot.cache.FinishedGiveawayCache;

@RestController
@RequestMapping("/test")
public class Test {
    private final FinishedGiveawayCache giveawayCache;

    public Test(FinishedGiveawayCache giveawayCache) {
        this.giveawayCache = giveawayCache;
    }
}
