package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RewardPayoutTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RewardPayout.class);
        RewardPayout rewardPayout1 = new RewardPayout();
        rewardPayout1.setId(1L);
        RewardPayout rewardPayout2 = new RewardPayout();
        rewardPayout2.setId(rewardPayout1.getId());
        assertThat(rewardPayout1).isEqualTo(rewardPayout2);
        rewardPayout2.setId(2L);
        assertThat(rewardPayout1).isNotEqualTo(rewardPayout2);
        rewardPayout1.setId(null);
        assertThat(rewardPayout1).isNotEqualTo(rewardPayout2);
    }
}
