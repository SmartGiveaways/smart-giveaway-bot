package pink.zak.giveawaybot.service.types;

import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.util.Random;

@UtilityClass
public class NumberUtils {
    private final Random random = new Random();

    public static BigInteger getRandomBigInteger(BigInteger max) {
        BigInteger randomNumber;
        do {
            randomNumber = new BigInteger(max.bitLength(), random);
        } while (randomNumber.compareTo(max) >= 0);
        return randomNumber.add(BigInteger.ONE); // Added to make inclusive
    }
}
