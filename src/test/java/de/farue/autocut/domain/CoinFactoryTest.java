package de.farue.autocut.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CoinFactoryTest {

    @Test
    void testTokenGeneration() {
        CoinFactory coinFactory = new CoinFactory();
        Coin coin = coinFactory.createNewCoin();
        Assertions.assertThat(coin.getToken()).matches("\\w{3,6}\\d{3}");
    }
}
