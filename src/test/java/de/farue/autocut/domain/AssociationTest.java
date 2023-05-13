package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssociationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Association.class);
        Association association1 = new Association();
        association1.setId(1L);
        Association association2 = new Association();
        association2.setId(association1.getId());
        assertThat(association1).isEqualTo(association2);
        association2.setId(2L);
        assertThat(association1).isNotEqualTo(association2);
        association1.setId(null);
        assertThat(association1).isNotEqualTo(association2);
    }
}
