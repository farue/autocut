package de.farue.autocut.repository;

import de.farue.autocut.domain.BroadcastMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BroadcastMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BroadcastMessageRepository extends JpaRepository<BroadcastMessage, Long> {}
