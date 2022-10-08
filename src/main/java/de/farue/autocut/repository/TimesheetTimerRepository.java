package de.farue.autocut.repository;

import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.domain.TimesheetTimer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TimesheetTimer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimesheetTimerRepository extends JpaRepository<TimesheetTimer, Long> {
    @Query("select timer from TimesheetTimer timer left join fetch timer.timesheet where timer.timesheet = :timesheet")
    Optional<TimesheetTimer> findOneByTimesheet(Timesheet timesheet);

    void deleteAllByTimesheet(Timesheet timesheet);
}
