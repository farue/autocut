package de.farue.autocut.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import de.farue.autocut.web.rest.TestUtil;

public class PortTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Port.class);
        Port port1 = new Port();
        port1.setId(1L);
        Port port2 = new Port();
        port2.setId(port1.getId());
        assertThat(port1).isEqualTo(port2);
        port2.setId(2L);
        assertThat(port1).isNotEqualTo(port2);
        port1.setId(null);
        assertThat(port1).isNotEqualTo(port2);
    }
}
