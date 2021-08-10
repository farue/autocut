package de.farue.autocut.repository;

import de.farue.autocut.domain.BroadcastMessage;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BroadcastMessage entity.
 */
@Repository
public interface BroadcastMessageRepository extends JpaRepository<BroadcastMessage, Long> {
    @Query(
        "select m from BroadcastMessage m where (m.start is null or m.start <= :timestamp) and (m.end is null or m.end > :timestamp) and (:includeUsersOnly = true or m.usersOnly = false)"
    )
    List<BroadcastMessage> findAllActiveAt(Instant timestamp, boolean includeUsersOnly);
}
