package de.farue.autocut.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import de.farue.autocut.web.rest.TestUtil;

public class CommunicationTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Communication.class);
        Communication communication1 = new Communication();
        communication1.setId(1L);
        Communication communication2 = new Communication();
        communication2.setId(communication1.getId());
        assertThat(communication1).isEqualTo(communication2);
        communication2.setId(2L);
        assertThat(communication1).isNotEqualTo(communication2);
        communication1.setId(null);
        assertThat(communication1).isNotEqualTo(communication2);
    }
}
