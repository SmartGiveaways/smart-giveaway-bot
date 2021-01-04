package pink.zak.giveawaybot.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Member could not be retrieved from JDA")
public class MemberNotFoundException extends RuntimeException {
}
