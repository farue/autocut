package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimesheetTimeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimesheetTime.class);
        TimesheetTime timesheetTime1 = new TimesheetTime();
        timesheetTime1.setId(1L);
        TimesheetTime timesheetTime2 = new TimesheetTime();
        timesheetTime2.setId(timesheetTime1.getId());
        assertThat(timesheetTime1).isEqualTo(timesheetTime2);
        timesheetTime2.setId(2L);
        assertThat(timesheetTime1).isNotEqualTo(timesheetTime2);
        timesheetTime1.setId(null);
        assertThat(timesheetTime1).isNotEqualTo(timesheetTime2);
    }
}
