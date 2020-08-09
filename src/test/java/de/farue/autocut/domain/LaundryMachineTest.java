package de.farue.autocut.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import de.farue.autocut.web.rest.TestUtil;

public class LaundryMachineTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LaundryMachine.class);
        LaundryMachine laundryMachine1 = new LaundryMachine();
        laundryMachine1.setId(1L);
        LaundryMachine laundryMachine2 = new LaundryMachine();
        laundryMachine2.setId(laundryMachine1.getId());
        assertThat(laundryMachine1).isEqualTo(laundryMachine2);
        laundryMachine2.setId(2L);
        assertThat(laundryMachine1).isNotEqualTo(laundryMachine2);
        laundryMachine1.setId(null);
        assertThat(laundryMachine1).isNotEqualTo(laundryMachine2);
    }
}
