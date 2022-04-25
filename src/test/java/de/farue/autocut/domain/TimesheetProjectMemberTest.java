package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimesheetProjectMemberTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimesheetProjectMember.class);
        TimesheetProjectMember timesheetProjectMember1 = new TimesheetProjectMember();
        timesheetProjectMember1.setId(1L);
        TimesheetProjectMember timesheetProjectMember2 = new TimesheetProjectMember();
        timesheetProjectMember2.setId(timesheetProjectMember1.getId());
        assertThat(timesheetProjectMember1).isEqualTo(timesheetProjectMember2);
        timesheetProjectMember2.setId(2L);
        assertThat(timesheetProjectMember1).isNotEqualTo(timesheetProjectMember2);
        timesheetProjectMember1.setId(null);
        assertThat(timesheetProjectMember1).isNotEqualTo(timesheetProjectMember2);
    }
}
