package de.farue.autocut.repository;

import de.farue.autocut.domain.TimesheetTimer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TimesheetTimer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimesheetTimerRepository extends JpaRepository<TimesheetTimer, Long> {}
