package de.farue.autocut.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {

    public static boolean isPositive(BigDecimal d) {
        return d != null && d.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isNotNegative(BigDecimal d) {
        return d != null && !isNegative(d);
    }

    public static boolean isNegative(BigDecimal d) {
        return d != null && d.compareTo(BigDecimal.ZERO) < 0;
    }

    public static boolean isNotPositive(BigDecimal d) {
        return d != null && !isPositive(d);
    }

    public static BigDecimal negative(BigDecimal d) {
        return isPositive(d) ? d.negate() : d;
    }

    public static BigDecimal positive(BigDecimal d) {
        return isNegative(d) ? d.negate() : d;
    }

    public static boolean isZero(BigDecimal d) {
        return BigDecimal.ZERO.compareTo(d) == 0;
    }
}
