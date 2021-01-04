package pink.zak.giveawaybot.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Guild could not be found in JDA")
public class GuildNotFoundException extends RuntimeException {
}
