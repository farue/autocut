package de.farue.autocut.repository;

import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.domain.TimesheetProjectMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TimesheetProjectMember entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimesheetProjectMemberRepository extends JpaRepository<TimesheetProjectMember, Long> {
    @Query("select distinct projectMember from TimesheetProjectMember projectMember where projectMember.timesheet = :timesheet")
    List<TimesheetProjectMember> findAllByTimesheet(Timesheet timesheet);
}
