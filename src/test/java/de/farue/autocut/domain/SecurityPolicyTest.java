package de.farue.autocut.domain;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityPolicyTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SecurityPolicy.class);
        SecurityPolicy securityPolicy1 = new SecurityPolicy();
        securityPolicy1.setId(1L);
        SecurityPolicy securityPolicy2 = new SecurityPolicy();
        securityPolicy2.setId(securityPolicy1.getId());
        assertThat(securityPolicy1).isEqualTo(securityPolicy2);
        securityPolicy2.setId(2L);
        assertThat(securityPolicy1).isNotEqualTo(securityPolicy2);
        securityPolicy1.setId(null);
        assertThat(securityPolicy1).isNotEqualTo(securityPolicy2);
    }
}
