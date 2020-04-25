package de.farue.autocut.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class BigDecimalUtilTest {

    @Test
    void testIsPositive() {
        Assertions.assertThat(BigDecimalUtil.isPositive(new BigDecimal("1.23"))).isTrue();
        Assertions.assertThat(BigDecimalUtil.isPositive(new BigDecimal("0.00"))).isFalse();
        Assertions.assertThat(BigDecimalUtil.isPositive(new BigDecimal("-1.23"))).isFalse();
    }

    @Test
    void testIsNotNegative() {
        Assertions.assertThat(BigDecimalUtil.isNotNegative(new BigDecimal("1.23"))).isTrue();
        Assertions.assertThat(BigDecimalUtil.isNotNegative(new BigDecimal("0.00"))).isTrue();
        Assertions.assertThat(BigDecimalUtil.isNotNegative(new BigDecimal("-1.23"))).isFalse();
    }

    @Test
    void testIsNegative() {
        Assertions.assertThat(BigDecimalUtil.isNegative(new BigDecimal("1.23"))).isFalse();
        Assertions.assertThat(BigDecimalUtil.isNegative(new BigDecimal("0.00"))).isFalse();
        Assertions.assertThat(BigDecimalUtil.isNegative(new BigDecimal("-1.23"))).isTrue();
    }

    @Test
    void testIsNotPositive() {
        Assertions.assertThat(BigDecimalUtil.isNotPositive(new BigDecimal("1.23"))).isFalse();
        Assertions.assertThat(BigDecimalUtil.isNotPositive(new BigDecimal("0.00"))).isTrue();
        Assertions.assertThat(BigDecimalUtil.isNotPositive(new BigDecimal("-1.23"))).isTrue();
    }

    @Test
    void testNegative() {
        Assertions.assertThat(BigDecimalUtil.negative(new BigDecimal("1.23"))).isNegative();
        Assertions.assertThat(BigDecimalUtil.negative(new BigDecimal("0.00"))).isZero();
        Assertions.assertThat(BigDecimalUtil.negative(new BigDecimal("-1.23"))).isNegative();
    }

    @Test
    void testPositive() {
        Assertions.assertThat(BigDecimalUtil.positive(new BigDecimal("1.23"))).isPositive();
        Assertions.assertThat(BigDecimalUtil.positive(new BigDecimal("0.00"))).isZero();
        Assertions.assertThat(BigDecimalUtil.positive(new BigDecimal("-1.23"))).isPositive();
    }
}
