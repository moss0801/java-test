package spock;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 계산기
 */
public class Calculator {
    public static long calculate(long amount , float rate, RoundingMode roundingMode) {
        if (amount < 0) {
            throw new NegativeNumberNotAllowException(amount);
        }
        return BigDecimal.valueOf(amount * rate * 0.01)
                .setScale(0, roundingMode ).longValue();
    }
}
