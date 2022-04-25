package de.farue.autocut.repository;

import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.Timesheet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Timesheet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
    Optional<Timesheet> findFirstByMember(Tenant member);
}
