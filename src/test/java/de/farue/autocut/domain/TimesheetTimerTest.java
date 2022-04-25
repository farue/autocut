package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimesheetTimerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimesheetTimer.class);
        TimesheetTimer timesheetTimer1 = new TimesheetTimer();
        timesheetTimer1.setId(1L);
        TimesheetTimer timesheetTimer2 = new TimesheetTimer();
        timesheetTimer2.setId(timesheetTimer1.getId());
        assertThat(timesheetTimer1).isEqualTo(timesheetTimer2);
        timesheetTimer2.setId(2L);
        assertThat(timesheetTimer1).isNotEqualTo(timesheetTimer2);
        timesheetTimer1.setId(null);
        assertThat(timesheetTimer1).isNotEqualTo(timesheetTimer2);
    }
}
