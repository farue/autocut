package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WashHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WashHistory.class);
        WashHistory washHistory1 = new WashHistory();
        washHistory1.setId(1L);
        WashHistory washHistory2 = new WashHistory();
        washHistory2.setId(washHistory1.getId());
        assertThat(washHistory1).isEqualTo(washHistory2);
        washHistory2.setId(2L);
        assertThat(washHistory1).isNotEqualTo(washHistory2);
        washHistory1.setId(null);
        assertThat(washHistory1).isNotEqualTo(washHistory2);
    }
}
