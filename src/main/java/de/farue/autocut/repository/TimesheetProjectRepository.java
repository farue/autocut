package de.farue.autocut.repository;

import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.domain.TimesheetProject;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TimesheetProject entity.
 */
@Repository
public interface TimesheetProjectRepository extends JpaRepository<TimesheetProject, Long> {
    @Query(
        value = "select distinct timesheetProject from TimesheetProject timesheetProject left join fetch timesheetProject.tasks",
        countQuery = "select count(distinct timesheetProject) from TimesheetProject timesheetProject"
    )
    Page<TimesheetProject> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct timesheetProject from TimesheetProject timesheetProject left join fetch timesheetProject.tasks")
    List<TimesheetProject> findAllWithEagerRelationships();

    @Query(
        "select timesheetProject from TimesheetProject timesheetProject left join fetch timesheetProject.tasks where timesheetProject.id =:id"
    )
    Optional<TimesheetProject> findOneWithEagerRelationships(@Param("id") Long id);

    @Query(
        "select distinct project from TimesheetProject project " +
        "left join fetch project.tasks task " +
        "where project in (" +
        "select projectMember.project from TimesheetProjectMember projectMember where projectMember.timesheet = :timesheet)"
    )
    List<TimesheetProject> findAllByTimesheet(Timesheet timesheet);

    // We have to include TimesheetTime in the selection because we order by it. But then only the pair (project, time)
    // is distinct, not the project itself anymore.
    @Query(
        "select distinct project, time from TimesheetProject project " +
        "left join fetch project.tasks task " +
        "left join TimesheetTime time on project.id = time.id " +
        "where project in (" +
        "  select projectMember.project from TimesheetProjectMember projectMember " +
        "  where projectMember.timesheet = :timesheet) " +
        "order by time.id desc"
    )
    List<TimesheetProject> findAllByTimesheetOrderByLastUsed(Timesheet timesheet);
}
