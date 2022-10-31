package de.farue.autocut.repository;

import de.farue.autocut.domain.RewardPayout;
import de.farue.autocut.domain.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the RewardPayout entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RewardPayoutRepository extends JpaRepository<RewardPayout, Long> {
    @Query("select sum(p.time) from RewardPayout p where p.timesheet = :timesheet")
    Long rewardedTime(Timesheet timesheet);
}
