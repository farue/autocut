package de.farue.autocut.utils;

import static de.farue.autocut.utils.BigDecimalUtil.compare;
import static de.farue.autocut.utils.BigDecimalUtil.modify;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BigDecimalUtilTest {

    @Nested
    class Compare {

        @Test
        void testIsPositive() {
            Assertions.assertThat(compare(new BigDecimal("1.23")).isPositive()).isTrue();
            Assertions.assertThat(compare(new BigDecimal("0.00")).isPositive()).isFalse();
            Assertions.assertThat(compare(new BigDecimal("-1.23")).isPositive()).isFalse();
        }

        @Test
        void testIsNotNegative() {
            Assertions.assertThat(compare(new BigDecimal("1.23")).isNotNegative()).isTrue();
            Assertions.assertThat(compare(new BigDecimal("0.00")).isNotNegative()).isTrue();
            Assertions.assertThat(compare(new BigDecimal("-1.23")).isNotNegative()).isFalse();
        }

        @Test
        void testIsNegative() {
            Assertions.assertThat(compare(new BigDecimal("1.23")).isNegative()).isFalse();
            Assertions.assertThat(compare(new BigDecimal("0.00")).isNegative()).isFalse();
            Assertions.assertThat(compare(new BigDecimal("-1.23")).isNegative()).isTrue();
        }

        @Test
        void testIsNotPositive() {
            Assertions.assertThat(compare(new BigDecimal("1.23")).isNotPositive()).isFalse();
            Assertions.assertThat(compare(new BigDecimal("0.00")).isNotPositive()).isTrue();
            Assertions.assertThat(compare(new BigDecimal("-1.23")).isNotPositive()).isTrue();
        }

        @Test
        void testIsEqual() {
            Assertions.assertThat(compare(null).isEqualTo(null)).isTrue();
            Assertions.assertThat(compare(null).isEqualTo(new BigDecimal("1.23"))).isFalse();
            Assertions.assertThat(compare(new BigDecimal("1.23")).isEqualTo(new BigDecimal("1.23"))).isTrue();
            Assertions.assertThat(compare(new BigDecimal("1.20000")).isEqualTo(new BigDecimal("1.2"))).isTrue();
        }

        @Test
        void testIsGreaterThan() {
            Assertions.assertThat(compare(null).isGreaterThan(null)).isFalse();
            Assertions.assertThat(compare(null).isGreaterThan(new BigDecimal("1.23"))).isFalse();
            Assertions.assertThat(compare(new BigDecimal("1.23")).isGreaterThan(new BigDecimal("1.23"))).isFalse();
            Assertions.assertThat(compare(new BigDecimal("1.201")).isGreaterThan(new BigDecimal("1.20000"))).isTrue();
            Assertions.assertThat(compare(new BigDecimal("5")).isGreaterThan(new BigDecimal("1.23"))).isTrue();
        }

        @Test
        void testIsSmallerThan() {
            Assertions.assertThat(compare(null).isSmallerThan(null)).isFalse();
            Assertions.assertThat(compare(null).isSmallerThan(new BigDecimal("1.23"))).isFalse();
            Assertions.assertThat(compare(new BigDecimal("1.23")).isSmallerThan(new BigDecimal("1.23"))).isFalse();
            Assertions.assertThat(compare(new BigDecimal("1.20000")).isSmallerThan(new BigDecimal("1.201"))).isTrue();
            Assertions.assertThat(compare(new BigDecimal("1.23")).isSmallerThan(new BigDecimal("5"))).isTrue();
        }
    }

    @Nested
    class Modify {

        @Test
        void testNegative() {
            Assertions.assertThat(modify(new BigDecimal("1.23")).negative()).isNegative();
            Assertions.assertThat(modify(new BigDecimal("0.00")).negative()).isZero();
            Assertions.assertThat(modify(new BigDecimal("-1.23")).negative()).isNegative();
        }

        @Test
        void testPositive() {
            Assertions.assertThat(modify(new BigDecimal("1.23")).positive()).isPositive();
            Assertions.assertThat(modify(new BigDecimal("0.00")).positive()).isZero();
            Assertions.assertThat(modify(new BigDecimal("-1.23")).positive()).isPositive();
        }
    }
}
