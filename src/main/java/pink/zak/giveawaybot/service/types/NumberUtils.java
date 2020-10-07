package pink.zak.giveawaybot.service.types;

import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.util.Random;

@UtilityClass
public class NumberUtils {

    public static BigInteger getRandomBigInteger(BigInteger max) {
        BigInteger randomNumber;
        do {
            randomNumber = new BigInteger(max.bitLength(), new Random());
        } while (randomNumber.compareTo(max) >= 0);
        return randomNumber.add(BigInteger.ONE); // Added to make inclusive
    }
}
