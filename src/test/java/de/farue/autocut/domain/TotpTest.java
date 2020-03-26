package de.farue.autocut.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import de.farue.autocut.web.rest.TestUtil;

public class TotpTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Totp.class);
        Totp totp1 = new Totp();
        totp1.setId(1L);
        Totp totp2 = new Totp();
        totp2.setId(totp1.getId());
        assertThat(totp1).isEqualTo(totp2);
        totp2.setId(2L);
        assertThat(totp1).isNotEqualTo(totp2);
        totp1.setId(null);
        assertThat(totp1).isNotEqualTo(totp2);
    }
}
