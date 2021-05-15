package de.farue.autocut.repository;

import de.farue.autocut.domain.ScheduledJob;
import de.farue.autocut.domain.enumeration.ScheduledJobStatus;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long> {
    Optional<ScheduledJob> findFirstByNameAndStatusOrderByEndTimeDesc(String name, ScheduledJobStatus status);

    @Modifying
    @Query("update ScheduledJob j set j.status = :status where j.id = :id")
    void updateStatus(long id, ScheduledJobStatus status);

    @Modifying
    @Query(
        "update ScheduledJob j set j.endTime = :endTime, j.status = de.farue.autocut.domain.enumeration.ScheduledJobStatus.COMPLETED where j.id = :id"
    )
    void updateStatusCompleted(long id, Instant endTime);

    @Modifying
    @Query(
        "update ScheduledJob j set j.endTime = :endTime, j.status = de.farue.autocut.domain.enumeration.ScheduledJobStatus.ERROR, j.exitMessage = :exitMessage where j.id = :id"
    )
    void updateStatusError(long id, Instant endTime, String exitMessage);

    @Modifying
    @Query("update ScheduledJob j set j.dataStartTime = :dataStartTime where j.id = :id")
    void updateDataStartTime(long id, Instant dataStartTime);

    @Modifying
    @Query("update ScheduledJob j set j.dataEndTime = :dataEndTime where j.id = :id")
    void updateDataEndTime(long id, Instant dataEndTime);
}
