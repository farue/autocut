package de.farue.autocut.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {

    public static Compare compare(BigDecimal d) {
        return new Compare(d);
    }

    public static Modify modify(BigDecimal d) {
        return new Modify(d);
    }

    public static class Compare {

        private final BigDecimal d;

        public Compare(BigDecimal d) {
            this.d = d;
        }

        public boolean isPositive() {
            return d != null && d.compareTo(BigDecimal.ZERO) > 0;
        }

        public boolean isNotNegative() {
            return d != null && !isNegative();
        }

        public boolean isNegative() {
            return d != null && d.compareTo(BigDecimal.ZERO) < 0;
        }

        public boolean isNotPositive() {
            return d != null && !isPositive();
        }

        public boolean isZero() {
            return BigDecimal.ZERO.compareTo(d) == 0;
        }

        public boolean isEqualTo(BigDecimal other) {
            if (d == null || other == null) {
                return d == other;
            }
            return d.compareTo(other) == 0;
        }

        public boolean isNotEqualTo(BigDecimal other) {
            return !isEqualTo(other);
        }

        public boolean isGreaterThan(BigDecimal other) {
            if (d == null || other == null) {
                return false;
            }
            return d.compareTo(other) > 0;
        }

        public boolean isSmallerThan(BigDecimal other) {
            if (d == null || other == null) {
                return false;
            }
            return d.compareTo(other) < 0;
        }
    }

    public static class Modify {

        private final BigDecimal d;

        public Modify(BigDecimal d) {
            this.d = d;
        }

        public BigDecimal negative() {
            return new Compare(d).isPositive() ? d.negate() : d;
        }

        public BigDecimal positive() {
            return new Compare(d).isNegative() ? d.negate() : d;
        }
    }
}
