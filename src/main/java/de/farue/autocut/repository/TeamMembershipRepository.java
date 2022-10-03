package de.farue.autocut.repository;

import de.farue.autocut.domain.Team;
import de.farue.autocut.domain.TeamMembership;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TeamMembership entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {
    List<TeamMembership> findAllByTeam(Team team);
}
