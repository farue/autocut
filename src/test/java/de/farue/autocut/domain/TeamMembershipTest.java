package de.farue.autocut.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeamMembershipTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamMembership.class);
        TeamMembership teamMembership1 = new TeamMembership();
        teamMembership1.setId(1L);
        TeamMembership teamMembership2 = new TeamMembership();
        teamMembership2.setId(teamMembership1.getId());
        assertThat(teamMembership1).isEqualTo(teamMembership2);
        teamMembership2.setId(2L);
        assertThat(teamMembership1).isNotEqualTo(teamMembership2);
        teamMembership1.setId(null);
        assertThat(teamMembership1).isNotEqualTo(teamMembership2);
    }
}
