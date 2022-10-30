package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RewardTimeReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RewardTimeReview.class);
        RewardTimeReview rewardTimeReview1 = new RewardTimeReview();
        rewardTimeReview1.setId(1L);
        RewardTimeReview rewardTimeReview2 = new RewardTimeReview();
        rewardTimeReview2.setId(rewardTimeReview1.getId());
        assertThat(rewardTimeReview1).isEqualTo(rewardTimeReview2);
        rewardTimeReview2.setId(2L);
        assertThat(rewardTimeReview1).isNotEqualTo(rewardTimeReview2);
        rewardTimeReview1.setId(null);
        assertThat(rewardTimeReview1).isNotEqualTo(rewardTimeReview2);
    }
}
