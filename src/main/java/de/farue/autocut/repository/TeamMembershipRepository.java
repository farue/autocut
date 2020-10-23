package de.farue.autocut.repository;

import de.farue.autocut.domain.TeamMembership;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the TeamMembership entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {
}
