package de.farue.autocut.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import de.farue.autocut.web.rest.TestUtil;

public class CoinTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Coin.class);
        Coin coin1 = new Coin();
        coin1.setId(1L);
        Coin coin2 = new Coin();
        coin2.setId(coin1.getId());
        assertThat(coin1).isEqualTo(coin2);
        coin2.setId(2L);
        assertThat(coin1).isNotEqualTo(coin2);
        coin1.setId(null);
        assertThat(coin1).isNotEqualTo(coin2);
    }
}
