package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LeaseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Lease.class);
        Lease lease1 = new Lease();
        lease1.setId(1L);
        Lease lease2 = new Lease();
        lease2.setId(lease1.getId());
        assertThat(lease1).isEqualTo(lease2);
        lease2.setId(2L);
        assertThat(lease1).isNotEqualTo(lease2);
        lease1.setId(null);
        assertThat(lease1).isNotEqualTo(lease2);
    }
}
