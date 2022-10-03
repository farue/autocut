package de.farue.autocut.repository;

import de.farue.autocut.domain.Team;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Team entity.
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("select t from Team t where lower(t.name) = lower(:name)")
    Optional<Team> findByName(String name);
}
