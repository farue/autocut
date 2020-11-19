package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.farue.autocut.web.rest.TestUtil;

public class RegistrationItemTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RegistrationItem.class);
        RegistrationItem registrationItem1 = new RegistrationItem();
        registrationItem1.setId(1L);
        RegistrationItem registrationItem2 = new RegistrationItem();
        registrationItem2.setId(registrationItem1.getId());
        assertThat(registrationItem1).isEqualTo(registrationItem2);
        registrationItem2.setId(2L);
        assertThat(registrationItem1).isNotEqualTo(registrationItem2);
        registrationItem1.setId(null);
        assertThat(registrationItem1).isNotEqualTo(registrationItem2);
    }
}
