package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NetworkSwitchStatusTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NetworkSwitchStatus.class);
        NetworkSwitchStatus networkSwitchStatus1 = new NetworkSwitchStatus();
        networkSwitchStatus1.setId(1L);
        NetworkSwitchStatus networkSwitchStatus2 = new NetworkSwitchStatus();
        networkSwitchStatus2.setId(networkSwitchStatus1.getId());
        assertThat(networkSwitchStatus1).isEqualTo(networkSwitchStatus2);
        networkSwitchStatus2.setId(2L);
        assertThat(networkSwitchStatus1).isNotEqualTo(networkSwitchStatus2);
        networkSwitchStatus1.setId(null);
        assertThat(networkSwitchStatus1).isNotEqualTo(networkSwitchStatus2);
    }
}
