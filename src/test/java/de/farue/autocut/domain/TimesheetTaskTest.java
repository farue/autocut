package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimesheetTaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimesheetTask.class);
        TimesheetTask timesheetTask1 = new TimesheetTask();
        timesheetTask1.setId(1L);
        TimesheetTask timesheetTask2 = new TimesheetTask();
        timesheetTask2.setId(timesheetTask1.getId());
        assertThat(timesheetTask1).isEqualTo(timesheetTask2);
        timesheetTask2.setId(2L);
        assertThat(timesheetTask1).isNotEqualTo(timesheetTask2);
        timesheetTask1.setId(null);
        assertThat(timesheetTask1).isNotEqualTo(timesheetTask2);
    }
}
