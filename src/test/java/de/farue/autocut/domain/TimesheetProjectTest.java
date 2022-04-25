package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimesheetProjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimesheetProject.class);
        TimesheetProject timesheetProject1 = new TimesheetProject();
        timesheetProject1.setId(1L);
        TimesheetProject timesheetProject2 = new TimesheetProject();
        timesheetProject2.setId(timesheetProject1.getId());
        assertThat(timesheetProject1).isEqualTo(timesheetProject2);
        timesheetProject2.setId(2L);
        assertThat(timesheetProject1).isNotEqualTo(timesheetProject2);
        timesheetProject1.setId(null);
        assertThat(timesheetProject1).isNotEqualTo(timesheetProject2);
    }
}
