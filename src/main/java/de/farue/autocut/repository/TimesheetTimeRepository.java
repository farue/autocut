package de.farue.autocut.repository;

import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.domain.TimesheetProject;
import de.farue.autocut.domain.TimesheetTime;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TimesheetTime entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimesheetTimeRepository extends JpaRepository<TimesheetTime, Long> {
    Page<TimesheetTime> findAllByTimesheet(Timesheet timesheet, Pageable pageable);

    @Query(
        "select t from TimesheetTime t " +
        "where t.id in " +
        "  (select max(t2.id) from TimesheetTime t2 " +
        "  where t2.timesheet = :timesheet and t2.project = :project " +
        "  group by t2.description) " +
        "order by t.id desc"
    )
    List<TimesheetTime> findByTimesheetOrderByLastUsed(Timesheet timesheet, TimesheetProject project, Pageable pageable);

    @Query("select t from TimesheetTime t left join fetch t.timesheet where t.start >= :earliest and t.start < :latest")
    List<TimesheetTime> findAllByEndAfterAndEndBefore(Instant earliest, Instant latest);

    @Query("select sum(t.effectiveTime) from TimesheetTime t where t.timesheet = :timesheet")
    Long getSumTimes(Timesheet timesheet);

    @Query("select sum(t.effectiveTime) from TimesheetTime t where t.timesheet = :timesheet and t.start >= :earliest and t.start < :latest")
    Long getSumTimes(Timesheet timesheet, Instant earliest, Instant latest);
}
