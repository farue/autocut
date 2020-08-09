package de.farue.autocut.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import de.farue.autocut.web.rest.TestUtil;

public class LaundryMachineProgramTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LaundryMachineProgram.class);
        LaundryMachineProgram laundryMachineProgram1 = new LaundryMachineProgram();
        laundryMachineProgram1.setId(1L);
        LaundryMachineProgram laundryMachineProgram2 = new LaundryMachineProgram();
        laundryMachineProgram2.setId(laundryMachineProgram1.getId());
        assertThat(laundryMachineProgram1).isEqualTo(laundryMachineProgram2);
        laundryMachineProgram2.setId(2L);
        assertThat(laundryMachineProgram1).isNotEqualTo(laundryMachineProgram2);
        laundryMachineProgram1.setId(null);
        assertThat(laundryMachineProgram1).isNotEqualTo(laundryMachineProgram2);
    }
}
