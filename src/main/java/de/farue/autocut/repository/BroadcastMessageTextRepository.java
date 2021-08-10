package de.farue.autocut.repository;

import de.farue.autocut.domain.BroadcastMessageText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BroadcastMessageText entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BroadcastMessageTextRepository extends JpaRepository<BroadcastMessageText, Long> {}
