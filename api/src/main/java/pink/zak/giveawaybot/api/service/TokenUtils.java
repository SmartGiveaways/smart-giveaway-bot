package pink.zak.giveawaybot.api.service;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtils {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public static String generate() {
        byte[] randomBytes = new byte[96];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
