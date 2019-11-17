package de.farue.autocut.domain;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NetworkSwitchTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NetworkSwitch.class);
        NetworkSwitch networkSwitch1 = new NetworkSwitch();
        networkSwitch1.setId(1L);
        NetworkSwitch networkSwitch2 = new NetworkSwitch();
        networkSwitch2.setId(networkSwitch1.getId());
        assertThat(networkSwitch1).isEqualTo(networkSwitch2);
        networkSwitch2.setId(2L);
        assertThat(networkSwitch1).isNotEqualTo(networkSwitch2);
        networkSwitch1.setId(null);
        assertThat(networkSwitch1).isNotEqualTo(networkSwitch2);
    }
}
