package de.farue.autocut.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import de.farue.autocut.web.rest.TestUtil;

public class TenantCommunicationTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TenantCommunication.class);
        TenantCommunication tenantCommunication1 = new TenantCommunication();
        tenantCommunication1.setId(1L);
        TenantCommunication tenantCommunication2 = new TenantCommunication();
        tenantCommunication2.setId(tenantCommunication1.getId());
        assertThat(tenantCommunication1).isEqualTo(tenantCommunication2);
        tenantCommunication2.setId(2L);
        assertThat(tenantCommunication1).isNotEqualTo(tenantCommunication2);
        tenantCommunication1.setId(null);
        assertThat(tenantCommunication1).isNotEqualTo(tenantCommunication2);
    }
}
