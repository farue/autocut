package de.farue.autocut.domain;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TeamMemberTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamMember.class);
        TeamMember teamMember1 = new TeamMember();
        teamMember1.setId(1L);
        TeamMember teamMember2 = new TeamMember();
        teamMember2.setId(teamMember1.getId());
        assertThat(teamMember1).isEqualTo(teamMember2);
        teamMember2.setId(2L);
        assertThat(teamMember1).isNotEqualTo(teamMember2);
        teamMember1.setId(null);
        assertThat(teamMember1).isNotEqualTo(teamMember2);
    }
}
