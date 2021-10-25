package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LaundryProgramTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LaundryProgram.class);
        LaundryProgram laundryProgram1 = new LaundryProgram();
        laundryProgram1.setId(1L);
        LaundryProgram laundryProgram2 = new LaundryProgram();
        laundryProgram2.setId(laundryProgram1.getId());
        assertThat(laundryProgram1).isEqualTo(laundryProgram2);
        laundryProgram2.setId(2L);
        assertThat(laundryProgram1).isNotEqualTo(laundryProgram2);
        laundryProgram1.setId(null);
        assertThat(laundryProgram1).isNotEqualTo(laundryProgram2);
    }
}
