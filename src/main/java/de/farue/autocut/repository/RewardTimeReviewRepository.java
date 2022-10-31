package de.farue.autocut.repository;

import de.farue.autocut.domain.RewardTimeReview;
import de.farue.autocut.domain.Timesheet;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the RewardTimeReview entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RewardTimeReviewRepository extends JpaRepository<RewardTimeReview, Long> {
    @Query(
        "select sum(r.timesheetTime.effectiveTime)" +
        " from RewardTimeReview r" +
        " where r.timesheetTime.timesheet = :timesheet" +
        "   and r.timesheetTime.start >= :earliest" +
        "   and r.timesheetTime.end < :latest" +
        "   and (r.status = de.farue.autocut.domain.enumeration.RewardTimeReviewStatus.NOK or r.status = de.farue.autocut.domain.enumeration.RewardTimeReviewStatus.PENDING)"
    )
    Long timeExcludedFromReward(Timesheet timesheet, Instant earliest, Instant latest);
}
