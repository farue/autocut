package de.farue.autocut.repository;

import de.farue.autocut.domain.TimesheetProject;
import de.farue.autocut.domain.TimesheetTask;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TimesheetTask entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimesheetTaskRepository extends JpaRepository<TimesheetTask, Long> {
    // We have to include TimesheetTime in the selection because we order by it. But then only the pair (task, time)
    // is distinct, not the task itself anymore.
    @Query(
        "select distinct task, time from TimesheetTask task " +
        "left join fetch task.projects project " +
        "left join TimesheetTime time on task = time.task " +
        "where project = :project and task.enabled = true " +
        "order by time.id desc"
    )
    List<TimesheetTask> findAllByTimesheetProjectOrderByLastUsed(TimesheetProject project);

    @Query(
        "select distinct task from TimesheetTask task " +
        "left join fetch task.projects project " +
        "where project = :project and task.enabled = true"
    )
    List<TimesheetTask> findAllByTimesheetProject(TimesheetProject project);
}
